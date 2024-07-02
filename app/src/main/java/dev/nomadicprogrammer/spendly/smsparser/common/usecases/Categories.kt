package dev.nomadicprogrammer.spendly.smsparser.common.usecases

import com.google.gson.annotations.SerializedName

data class Categories(

	@field:SerializedName("CashInflow")
	val cashInflow: List<TransactionCategory.CashInflow>,

	@field:SerializedName("CashOutflow")
	val cashOutflow: List<TransactionCategory.CashOutflow>
)

sealed class TransactionCategory(@field:SerializedName("name") val name: String){
	data object Other : TransactionCategory("Other")

	class CashOutflow(name: String) : TransactionCategory(name)

	class CashInflow(name: String) : TransactionCategory(name)
}
