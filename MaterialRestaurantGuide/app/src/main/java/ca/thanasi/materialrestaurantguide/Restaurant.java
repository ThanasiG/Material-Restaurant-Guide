package ca.thanasi.materialrestaurantguide;

public class Restaurant {
    public int id;
    public String name;
    public String address;
    public String phone;
    public String desc;
    public String tags[];
    public float rating;

    public Restaurant(int id, String name, String address, String phone, String desc, String tags[], float rating) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.desc = desc;
        this.tags = tags;
        this.rating = rating;

    }
    public Restaurant(int id) {
        this.id = id;
    }
    public String toString() {
        return this.name;
    }
}