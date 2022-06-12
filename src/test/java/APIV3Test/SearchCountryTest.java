package APIV3Test;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.jayway.jsonpath.JsonPath;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class SearchCountryTest {
	private Scanner c;
	RequestSpecification httpRequest;
  
	@BeforeTest
	public void Setup() {
		RestAssured.baseURI = "https://restcountries.com/v3.1";
		httpRequest = RestAssured.given();
	}
	
	@Test(dataProvider = "invalidName", dataProviderClass = CountryTestData.class)
	public void searchCountriesByInvalidName(String invalidName) {
		Assert.assertEquals(getCountriesByName(invalidName).getStatusCode(), 404, "Correct status code returned");
	}
	
	@Test(dataProvider = "validPartialName", dataProviderClass = CountryTestData.class)
	public void searchCountriesByValidPartialName(String name, String official1, String official2, String official3) {
		Response response = getCountriesByName(name);
		String name1 = JsonPath.read(response.asString(),"$[0].name.official").toString();	
		String name2 = JsonPath.read(response.asString(),"$[1].name.official").toString();	
		String name3 = JsonPath.read(response.asString(),"$[2].name.official").toString();	
		Assert.assertEquals(response.getStatusCode(), 200, "Correct status code returned");
		Assert.assertEquals(name1, official1, "Correct the first country name returned");
		Assert.assertEquals(name2, official2, "Correct the second country name returned");
		Assert.assertEquals(name3, official3, "Correct the third country name returned");
	}
	
	@Test(dataProvider = "validName", dataProviderClass = CountryTestData.class)
	public void searchCountriesByValidName(String name, String common, String official) {
		Response response = getCountriesByName(name);
		String commonName = JsonPath.read(response.asString(),"$[0].name.common").toString();	
		String officialName = JsonPath.read(response.asString(),"$[0].name.official").toString();	
		Assert.assertEquals(response.getStatusCode(), 200, "Correct status code returned");
		Assert.assertEquals(commonName, common, "Correct common name returned");
		Assert.assertEquals(officialName, official, "Correct official name returned");
	}
	
	@Test(dataProvider = "invalidCode", dataProviderClass = CountryTestData.class)
	public void searchCountriesByInvalidCode(String invalidCode, int statusCode) {	
		Assert.assertEquals(getCountriesByCode(invalidCode).getStatusCode(), statusCode, "Correct status code returned");
	}
	
	@Test(dataProvider = "validCode", dataProviderClass = CountryTestData.class)
	public void searchCountriesByValidCode(String code, String common, String codev2, String codev3, String codeNumber, String countryCode) {
		Response response = getCountriesByCode(code);
		String commonName = JsonPath.read(response.asString(),"$[0].name.common").toString();	
		String cca2 = JsonPath.read(response.asString(),"$[0].cca2").toString();	
		String cca3 = JsonPath.read(response.asString(),"$[0].cca3").toString();	
		String ccn3 = JsonPath.read(response.asString(),"$[0].ccn3").toString();	
		String cioc = JsonPath.read(response.asString(),"$[0].cioc").toString();	
		Assert.assertEquals(response.getStatusCode(), 200, "Correct status code returned");
		Assert.assertEquals(commonName, common, "Correct common name returned");
		Assert.assertEquals(cca2, codev2, "Correct code version 2 returned");
		Assert.assertEquals(cca3, codev3, "Correct code version 3 returned");
		Assert.assertEquals(ccn3, codeNumber, "Correct code number returned");
		Assert.assertEquals(cioc, countryCode, "Correct cioc code returned");
	}

	@Test
	public void searchCountries() {
		c = new Scanner(System.in);
		while (true) {
			System.out.println("Please choose option below by typing 1, 2 or 3");
			System.out.println("1. Search by name");
			System.out.println("2. Search by code");
			System.out.println("3. Exit");
			String option = c.nextLine();
			switch(option) {
				case "1": 
					System.out.print("Enter country name: ");
					String name = c.nextLine();
					if (checkNameFormat(name)) {
						verifyCountryResponse(getCountriesByName(name));
					} else {
						System.out.println("You entered invalid name. Name should include only alphabet characters and space but not only spaces");
					}
					break;
				case "2": 
					System.out.print("Enter country code: ");
					String code = c.nextLine();
					if (checkCodeFormat(code)) {
						verifyCountryResponse(getCountriesByCode(code));
					} else {
						System.out.println("You entered invalid code. Code should include only alphabet or numeric characters");
					}
					break;
				case "3":
					return;
				default:
					System.out.println("You enterered invalid input");
					break;
			}
		}
	}
	
	public Response getCountriesByName(String name) {
		return httpRequest.request(Method.GET, "/name/" + name);
	}
	
	public Response getCountriesByCode(String code) {
		return httpRequest.request(Method.GET, "/alpha/" + code);
	}
	
	public boolean checkNameFormat(String name) {
		if (name.length() < 1 || name.length() > 100) {
			return false;
		}
		boolean isEmpty = true;
		for (int i = 0; i < name.length(); i++) {
			if (name.charAt(i) != ' ') {
				isEmpty = false;
				break;
			}
		}
		if (isEmpty) {
			return false;
		}
		return name.matches("^[a-zA-Z\\s]*$");
	}
	
	public boolean checkCodeFormat(String code) {
		if (code.length() < 2 || code.length() > 3) {
			return false;
		}
		return code.matches("^[a-zA-Z0-9]*$");
	}
  
  	public void verifyCountryResponse(Response response) {
  		int statusCode = response.getStatusCode();
		if (statusCode == 200) {
			List<Map<String, ?>> items = JsonPath.read(response.asString(), "$");
			System.out.println(items.size());
			for (int i = 0; i < items.size(); i++) {
				String name = JsonPath.read(response.asString(),"$[" + i + "].name.common").toString();
				if (items.get(i).containsKey("capital")) {
					String capital = JsonPath.read(response.asString(),"$[" + i + "].capital[0]").toString();
					System.out.println("Country " + name + " found with capital city: " + capital);
				} else {
					System.out.println("Country " + name + " found with no capital city");
				}
			}
		} else if (statusCode == 404) {
			System.out.println("Country not found");
		} else if (statusCode == 400) {
			System.out.println("Bad request");
		} else {
			System.out.println("Error when searching countries");
		}
  	}
}
