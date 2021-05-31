package it.eng.idsa.dataapp.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.fraunhofer.iais.eis.ContractAgreementMessage;
import de.fraunhofer.iais.eis.ContractRequestMessage;
import de.fraunhofer.iais.eis.Message;

@Component
public class MessageUtil {
	
	@Value("${application.dataLakeDirectory}") 
	private Path dataLakeDirectory;
	
	private static final Logger logger = LoggerFactory.getLogger(MessageUtil.class);
	
	public String createResponsePayload(Message requestHeader) {
		if(requestHeader instanceof ContractRequestMessage) {
			return createContractAgreement(dataLakeDirectory);
		} else if(requestHeader instanceof ContractAgreementMessage) {
			return null;
		} else {
			return createResponsePayload();
		}
	}
	
	public String createResponsePayload(String requestHeader) {
		if(requestHeader.contains(ContractRequestMessage.class.getSimpleName())) {
			return createContractAgreement(dataLakeDirectory);
		} else if(requestHeader.contains(ContractAgreementMessage.class.getSimpleName())) {
			return null;
		} else {
			return createResponsePayload();
		}
	}
	
	private  String createResponsePayload() {
		// Put check sum in the payload
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		String formattedDate = dateFormat.format(date);
		
		 Map<String, String> jsonObject = new HashMap<>();
         jsonObject.put("firstName", "John");
         jsonObject.put("lastName", "Doe");
         jsonObject.put("dateOfBirth", formattedDate);
         jsonObject.put("address", "591  My Street, My Country");
         jsonObject.put("email", "userId1@domine1.com");
         jsonObject.put("checksum", "ABC123 " + formattedDate);
         
         Random random = new Random();
         int cons =random.nextInt(100 - 10) + 10;
         jsonObject.put("mth_avg_cons",  cons + " kWh");
         
         Map<String, String> jsonObject2 = new HashMap<>();
         jsonObject2.put("firstName", "Bill");
         jsonObject2.put("lastName", "Doe");
         jsonObject2.put("dateOfBirth", formattedDate);
         jsonObject2.put("address", "592 My Street, My Country");
         jsonObject2.put("email", "userId2@domine1.com");
         jsonObject2.put("checksum", "ABC123 " + formattedDate);
         
         cons =random.nextInt(100 - 10) + 10;
         jsonObject2.put("mth_avg_cons",  cons + " kWh");
         
         ArrayList< Map<String, String>> dataset = new ArrayList<>();
         dataset.add(jsonObject2);
         dataset.add(jsonObject);
         Gson gson = new GsonBuilder().create();
         return gson.toJson(dataset);
	}
	
	private String createContractAgreement(Path dataLakeDirectory) {
		String contractAgreement = null;
		byte[] bytes;
		try {
			bytes = Files.readAllBytes(dataLakeDirectory.resolve("contract_agreement.json"));
			contractAgreement = IOUtils.toString(bytes, "UTF8");
		} catch (IOException e) {
			logger.error("Error while reading contract agreement file from dataLakeDirectory {}", e);
		}
		return contractAgreement;
	}
}
