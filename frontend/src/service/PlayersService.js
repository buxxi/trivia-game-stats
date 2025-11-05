export class PlayersService {
    async getPlayers() {
        let response = await fetch("/trivia-stats/api/v1/players");
        let data = await response.json();
        return data;
    }

    async getPlayer(name) {
        let response = await fetch(`/trivia-stats/api/v1/players/${name}`);
        let data = await response.json();
        return data;
    }

    async setAliases(name, aliases) {
        await fetch(`/trivia-stats/api/v1/players/${name}/alias`, {
            headers: {
                'Content-Type' : 'application/json'
            },
            method: 'PUT',
            body: JSON.stringify(aliases)
        });
    }
};