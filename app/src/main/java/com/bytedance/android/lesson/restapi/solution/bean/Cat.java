package com.bytedance.android.lesson.restapi.solution.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Xavier.S
 * @date 2019.01.17 18:08
 */
public class Cat {

    // TODO-C1 (1) Implement your Cat Bean here according to the response json
    /**
     * {
     * "breeds": [],
     * "categories": [
     * {
     * "id": 4,
     * "name": "sunglasses"
     * }
     * ],
     * "id": "bk",
     * "url": "https://cdn2.thecatapi.com/images/bk.jpg"
     * }
     */
    @SerializedName("id")
    private String id;

    @SerializedName("url")
    private String url;

    @SerializedName("categories")
    private List<Category> categories;

    public static class Category {
        @SerializedName("id")
        private int id;

        @SerializedName("name")
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Category{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Cat{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
