export class CategoriesService {
    async getCategories() {
        let response = await fetch("/trivia-stats/api/v1/categories");
        let data = await response.json();
        return data;
    }
};