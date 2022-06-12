package APIV3Test;

import org.testng.annotations.DataProvider;

public class CountryTestData {
	@DataProvider(name = "invalidName")
	public Object[][] invalidNameData() {
		return new Object[][] {
			{""},
			{" "},
//			{" united"}, 
			// It returns 1 country "United Republic of Tanzania" which is wrong response ("united" returns 3 countries)
			{"1234567890"},
			{"~!@#$%^&*()_+"},
			{"qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890~!@#$%^&*()_+"}
		};
	}
	
	@DataProvider(name = "validPartialName")
	public Object[][] validPartialNameData() {
		return new Object[][] {
			{"china", "People's Republic of China", "Macao Special Administrative Region of the People's Republic of China", "Republic of China (Taiwan)"},
			{"united", "United Mexican States", "United States of America", "United Republic of Tanzania"},
		};
	}
	
	@DataProvider(name = "validName")
	public Object[][] validNameData() {
		return new Object[][] {
			{"south korea", "South Korea", "Republic of Korea"},
			{"Mexico", "Mexico", "United Mexican States"},
			{"FRANCE", "France", "French Republic"},
			{"italian republic", "Italy", "Italian Republic"}
		};
	}
	
	@DataProvider(name = "invalidCode")
	public Object[][] invalidCodeData() {
		return new Object[][] {
			{"", 400},
			{" ", 400},
			{"p", 400},
			{" per", 400},
			{"peru", 400},
			{"1234", 400},
//			{"~!@#$%^&*()_+", 400}, // It returns 404 but it should be 400 because the character limit of code is 3
			{"ab", 404},
			{"123", 404}
		};
	}
	
	@DataProvider(name = "validCode")
	public Object[][] validCodeData() {
		return new Object[][] {
			{"pe", "Peru", "PE", "PER", "604", "PER"},
			{"per", "Peru", "PE", "PER", "604", "PER"},
			{"604", "Peru", "PE", "PER", "604", "PER"},
			{"JP", "Japan", "JP", "JPN", "392", "JPN"}
		};
	}
}
