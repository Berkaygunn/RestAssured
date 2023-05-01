package GoRest;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class GoRestUsersTests {

    Faker randomUretici=new Faker();

    int userID;

    RequestSpecification reqSpec;

    @BeforeClass
    public void setup(){

        baseURI = "https://gorest.co.in/public/v2/users";
        //baseURI ="https://test.gorest.co.in/public/v2/users/";

        reqSpec = new RequestSpecBuilder()
                .addHeader("Authorization", "Bearer afb60a4d9ce56cf44b4ed8c8fe64297ebb89f1299e78c8916fab240d5c91fbb5")
                .setContentType(ContentType.JSON)
                .build();

    }

    @Test(enabled = false)
    public void addUser(){
        // POST https://gorest.co.in/public/v2/users
        //{"name":"{{$randomFullName}}", "gender":"male", "email":"{{$randomEmail}}", "status":"active"}
        // "Authorization: Bearer d0fd459d2dfe29aa3a9a31489fa8132ff68c28c282cb843ed3f1a92edf134317"

        String rndFullname=randomUretici.name().fullName();
        String rndEmail=randomUretici.internet().emailAddress();

        userID =
             given()
                       .spec(reqSpec)
                       .body("{\"name\":\""+rndFullname+"\", \"gender\":\"male\", \"email\":\""+rndEmail+"\", \"status\":\"active\"}")
                     //.log().uri()
                     //.log().body()

                       .when()
                       .post("")

                       .then()
                       .log().body()
                       .statusCode(201)
                       .contentType(ContentType.JSON)
                       .extract().path("id")
            ;
    }

    @Test
    public void addUserMap() {

        System.out.println("baseURI = " + baseURI);
        String rndFullname = randomUretici.name().fullName();
        String rndEmail = randomUretici.internet().emailAddress();

        Map<String,String> newUser=new HashMap<>();
        newUser.put("name",rndFullname);
        newUser.put("gender","male");
        newUser.put("email",rndEmail);
        newUser.put("status","active");

        userID =
                given()
                        .spec(reqSpec)
                        .body(newUser)
                        //.log().uri()
                        //.log().body()

                        .when()
                        .post("")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .contentType(ContentType.JSON)
                        .extract().path("id");
    }

    @Test(enabled = false)
    public void addUserClass() {
        String rndFullname = randomUretici.name().fullName();
        String rndEmail = randomUretici.internet().emailAddress();

        User newUser=new User();
        newUser.name=rndFullname;
        newUser.gender="male";
        newUser.email=rndEmail;
        newUser.status="active";

        userID =
                given()
                        .spec(reqSpec)
                        .body(newUser)
                        //.log().uri()
                        //.log().body()

                        .when()
                        .post("")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .contentType(ContentType.JSON)
                        .extract().path("id");
    }


    @Test(dependsOnMethods = "addUserMap")
    public void getUserByID(){

        given()
                .spec(reqSpec)

                .when()
                .get(""+userID)

                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id",equalTo(userID))
        ;
    }

    @Test(dependsOnMethods = "getUserByID")
    public void editUser(){

        Map<String,String> updateUser=new HashMap<>();
        updateUser.put("name", "berkay gün");

        given()
                .spec(reqSpec)
                .body(updateUser)

                .when()
                .put(""+userID)

                .then()
                .log().body()
                .statusCode(200)
                .body("id", equalTo(userID))
                .body("name", equalTo("berkay gün"))
        ;
    }

    @Test(dependsOnMethods = "editUser")
    public void deleteUser(){

        given()
                .spec(reqSpec)

                .when()
                .delete(""+userID)

                .then()
                .log().all()
                .statusCode(204)

        ;
    }

    @Test(dependsOnMethods = "deleteUser")
    public void deleteNegativeUser(){

        given()
                .spec(reqSpec)

                .when()
                .delete(""+userID)

                .then()
                .log().all()
                .statusCode(404)

        ;
    }

}
