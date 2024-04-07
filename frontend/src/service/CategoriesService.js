export class CategoriesService {
    async getCategories() {
        let response = await fetch("/api/v1/categories");
        let data = await response.json();
        return data;
    }
};