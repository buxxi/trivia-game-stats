export class GamessService {
    async getGames() {
        let response = await fetch("/trivia-stats/api/v1/games");
        let data = await response.json();
        return data;
    }

    async getGame(id) {
        let response = await fetch(`/trivia-stats/api/v1/games/${id}`);
        let data = await response.json();
        return data;
    }
};