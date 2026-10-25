import { mkdir, rm, writeFile } from 'node:fs/promises';
import { join } from 'node:path';
import { fileURLToPath } from 'node:url';

const avatarDirectory = fileURLToPath(new URL('../src/assets/avatars/', import.meta.url));
const apiUrl = 'https://api.github.com/repos/buxxi/trivia-game/contents/common/img/avatars?ref=master';
const headers = { 'User-Agent': 'trivia-game-stats-avatar-fetcher' };

const listingResponse = await fetch(apiUrl, { headers });
if (!listingResponse.ok) {
    throw new Error(`Failed to list avatars: ${listingResponse.status} ${listingResponse.statusText}`);
}

const files = await listingResponse.json();
const avatarFiles = files.filter(file => file.type === 'file' && file.name.endsWith('.png'));
if (avatarFiles.length === 0) {
    throw new Error('No avatar PNG files were found in the source repository.');
}

const downloads = await Promise.all(avatarFiles.map(async file => {
    const response = await fetch(file.download_url, { headers });
    if (!response.ok) {
        throw new Error(`Failed to download ${file.name}: ${response.status} ${response.statusText}`);
    }
    return [file.name, Buffer.from(await response.arrayBuffer())];
}));

await mkdir(avatarDirectory, { recursive: true });
await Promise.all(downloads.map(([name, content]) => writeFile(join(avatarDirectory, name), content)));
