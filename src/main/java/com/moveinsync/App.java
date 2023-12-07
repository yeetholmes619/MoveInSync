package com.moveinsync;

import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;
import redis.clients.jedis.*;
import redis.clients.jedis.params.GeoRadiusParam;
import redis.clients.jedis.GeoUnit;
import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.GeoRadiusResponse;

class Redis {
    protected Jedis jedis;
    protected String key;
    private String host;
    private int port;

    public Redis(String key, String host, int port) {
        this.key = key;
        this.host = host;
        this.port = port;
        this.jedis = new Jedis(host, port);
    }

    public void addLocation(String id, double lat, double lon) {
        jedis.geoadd(key, lon, lat, id);
    }

    public List<GeoCoordinate> getLocation(String id) {
        return jedis.geopos(key, id);
    }

    public void addTrip(String bookid, String custid, String driverid, double end_lat, double end_lon) {
        jedis.geoadd(key, end_lon, end_lat, bookid);
        jedis.hset(bookid, "custid", custid);
        jedis.hset(bookid, "driverid", driverid);
    }
    public String getHash(String bookid, String key) {
        return jedis.hget(bookid, key);
    }

    public void deleteID(String id) {
        jedis.zrem(key, id);
    }
}


class Admin extends Redis {
    public Admin(String key, String host, int port) {
        super(key, host, port);
    }

    public List<GeoRadiusResponse> getDrivers(double lat, double lon, double radius) {
        return jedis.georadius(key, lon, lat, radius, GeoUnit.KM, GeoRadiusParam.geoRadiusParam().withDist().sortAscending());
    }
}

class Account {
    private String name;
    private String email;
    private String phone;
    protected String id; 
    private String type; 
    protected Admin admin;
    public Logger logger;

    protected Redis tripReq;
    protected Redis trip;
    protected Redis driverLoc;

    public void setAll(String name, String email, String phone, String id) { 
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.id = id;
        this.tripReq = new Redis("tripReq", "localhost", 6379);
        this.trip = new Redis("trip", "localhost", 6379);
        this.driverLoc = new Redis("driverLoc", "localhost", 6379);
        this.logger = Logger.getLogger("Account");
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPhone() {
        return this.phone;
    }

    public String getId() {
        return this.id;
    }

}

class Customer extends Account {
    public Customer(String name, String email, String phone, String id) {
        super.setAll(name, email, phone, id);
    }

    public void bookRequest(String bookid, double lat_start, double lon_start, double lat_end, double lon_end) {
        tripReq.addLocation(bookid, lat_start, lon_start);
        tripReq.addLocation(bookid, lat_end, lon_end);

        admin = new Admin("driver-loc", "localhost", 6379);
        double radius = 5;
        List<GeoRadiusResponse> drivers = new ArrayList<GeoRadiusResponse>();
       // while (drivers.size() == 0) {
            drivers = admin.getDrivers(lat_start, lon_start, radius);
            //radius += 2.5;
        //}

        if (drivers.size() == 0) {
            this.logger.log(Level.INFO, "No drivers found");
        } else {
            this.logger.log(Level.INFO, "Drivers found");
            String driverID = drivers.get(0).getMemberByString();
            trip.addTrip(bookid, this.getId(), driverID, lat_end, lon_end);
            driverLoc.deleteID(driverID);
            this.logger.log(Level.INFO, driverID + " started trip with " + this.getId() + " to " + lat_end + "," + lon_end);
        }
        tripReq.deleteID(bookid);
    }

    public void endTrip(String bookid) {
        List<GeoCoordinate> end = trip.getLocation(bookid);
        double lat_end = end.get(0).getLatitude();
        double lon_end = end.get(0).getLongitude();

        String driverID = trip.getHash(bookid, "driverid");
        trip.deleteID(bookid);
        driverLoc.addLocation(driverID, lat_end, lon_end);

        this.logger.log(Level.INFO, driverID + " ended trip with " + this.getId() + " at " + lat_end + "," + lon_end);
    }
}

class Driver extends Account {
    public Driver(String name, String email, String phone, String id) {
        super.setAll(name, email, phone, id);
    }

    public void updateLocation(double lat, double lon) {
        driverLoc.addLocation(this.getId(), lat, lon);
    }

    public void logOff() {
        driverLoc.deleteID(this.getId());
    }
}

public class App {
    public static void main(String[] args) {
        Customer c1 = new Customer("customer1", "customer1@gmail.com", "123456789", "1");
        Customer c2 = new Customer("customer2", "customer2@gmail.com", "123456789", "2");
        Driver d1 = new Driver("driver1", "driver1@gmail.com", "123456789", "3");
        Driver d2 = new Driver("driver2", "driver2@gmail.com", "123456789", "4");
        Driver d3 = new Driver("driver3", "driver3@gmail.com", "123456789", "5");

        d1.updateLocation(26.555804, 73.005372);
        d2.updateLocation(26.517945, 73.139630);
        c1.bookRequest("bookid1", 26.555804, 73.005372, 26.542190, 72.934498);
        d3.updateLocation(26.509510, 73.195821);
        c2.bookRequest("bookid2", 26.555804, 73.005372, 26.542190, 72.934498);
        c1.endTrip("bookid1");
        c2.endTrip("bookid2");
    }
}

