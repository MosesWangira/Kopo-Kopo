class Transactions(var transactionId: String, var transactionAmount: Double, var transactionDate: Long) :
    Comparable<Transactions> {
    override fun compareTo(other: Transactions): Int {
        return transactionId.compareTo(other.transactionId)
    }
}