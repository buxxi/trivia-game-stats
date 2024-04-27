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
};