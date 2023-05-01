package Campus;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;


public class CtizenShip {

    String ctizenName;
    String CtizenID;
    Faker faker=new Faker();

    RequestSpecification recSpec;

    @BeforeClass
    public void login(){

        baseURI="https://test.mersys.io";

        Map<String,String> userCredential=new HashMap<>();
        userCredential.put("username","turkeyts");
        userCredential.put("password","TechnoStudy123");
        userCredential.put("rememberMe","true");

        Cookies cookies=
                given()
                        .contentType(ContentType.JSON)
                        .body(userCredential)

                        .when()
                        .post("/auth/login")

                        .then()
                        //.log().all()
                        .statusCode(200)
                        .extract().response().getDetailedCookies()
                ;

        recSpec= new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addCookies(cookies)
                .build();
    }

    @Test
    public void CreateCtizen(){

        Map<String,String> Ctizen=new HashMap<>();
        ctizenName=faker.address().firstName();
        Ctizen.put("name",ctizenName);
        Ctizen.put("shortName",faker.address().lastName());


        CtizenID=
        given()

                .spec(recSpec)
                .body(Ctizen)

                .when()
                .post("/school-service/api/citizenships")

                .then()
                .log().body()
                .statusCode(201)
                .extract().path("id")

        ;

        System.out.println("CtizenID = " + CtizenID);
    }

    @Test(dependsOnMethods = "CreateCtizen")
    public void CreateCtizenNegative(){

        Map<String,String> Ctizen=new HashMap<>();

        Ctizen.put("name",ctizenName);
        Ctizen.put("shortName",faker.address().lastName());


                given()

                        .spec(recSpec)
                        .body(Ctizen)

                        .when()
                        .post("/school-service/api/citizenships")

                        .then()
                        .log().body()
                        .statusCode(400)
                        .body("message",containsString("already"))

        ;

    }

    @Test(dependsOnMethods = "CreateCtizenNegative")
    public void EditCtizen(){

        Map<String,String> Ctizen=new HashMap<>();
        Ctizen.put("id",CtizenID);

        ctizenName="berkay"+faker.number().digits(4);
        Ctizen.put("name",ctizenName);
        Ctizen.put("shortName",faker.address().lastName());


        given()

                .spec(recSpec)
                .body(Ctizen)

                .when()
                .put("/school-service/api/citizenships")

                .then()
                .log().body()
                .statusCode(200)
        ;

    }

    @Test(dependsOnMethods = "EditCtizen")
    public void DeleteCtizen(){

        Map<String,String> Ctizen=new HashMap<>();
        Ctizen.put("id",CtizenID);

        Ctizen.put("name",ctizenName+faker.number().digits(4));
        Ctizen.put("shortName",faker.address().lastName());


        given()

                .spec(recSpec)
                .pathParam("CtizenID",CtizenID)

                .when()
                .delete("/school-service/api/citizenships/{CtizenID}")

                .then()
                .log().body()
                .statusCode(200)

        ;

    }

    @Test(dependsOnMethods = "DeleteCtizen")
    public void DeleteCtizenNegative(){

        Map<String,String> Ctizen=new HashMap<>();
        Ctizen.put("id",CtizenID);

        Ctizen.put("name",ctizenName+faker.number().digits(4));
        Ctizen.put("shortName",faker.address().lastName());


        given()

                .spec(recSpec)
                .pathParam("CtizenID",CtizenID)

                .when()
                .delete("/school-service/api/citizenships/{CtizenID}")

                .then()
                .log().body()
                .statusCode(400)
                .body("message",equalTo("Citizenship not found"))

        ;

    }



}
