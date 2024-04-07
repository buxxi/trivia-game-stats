export class StatisticsService {
    async getStatistics() {
        let response = await fetch("/api/v1/statistics");
        let data = await response.json();
        return data;
    }
};