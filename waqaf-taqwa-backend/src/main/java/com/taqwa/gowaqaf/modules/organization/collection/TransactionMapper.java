package com.taqwa.gowaqaf.modules.organization.collection;

import com.taqwa.gowaqaf.modules.organization.collection.dto.TransactionDetails;
import com.taqwa.gowaqaf.modules.organization.collection.entity.Transaction;

public class TransactionMapper {

	public static TransactionDetails mapToDetails(Transaction transaction) {
		TransactionDetails dto = new TransactionDetails();

		dto.setId(transaction.getId());
		dto.setBillingCode(transaction.getBillingCode());
		dto.setTransactionId(transaction.getTransactionId());
		dto.setAmount(transaction.getAmount());
		dto.setPaidAt(transaction.getPaidAt());
		dto.setStatus(transaction.getStatus());
		dto.setDonationType(transaction.getDonationType());

		return dto;
	}

}
