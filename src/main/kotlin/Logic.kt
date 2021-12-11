import DateConverter.convertToDays
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.util.ArrayList

object MyLogic {
    /**
     * Read CSV file
     * */
    @Throws(IOException::class)
    fun readAllLinesFromFile(path: String?): ArrayList<String> {
        val fileReader = FileReader(path!!)
        val bufferedReader = BufferedReader(fileReader)
        var line: String?
        while (bufferedReader.readLine().also { line = it } != null) {
            aList.add(line!!)
        }
        bufferedReader.close()
        return aList
    }

    /**
     * Convert CSV to transactions
     * */
    private fun convertToTransactions(transactionStrings: ArrayList<String>): ArrayList<Transactions> {
        val transactions: ArrayList<Transactions> = ArrayList<Transactions>()
        transactionStrings.removeAt(0)
        for (transactionString in transactionStrings) {
            val parts = transactionString.split(",").toTypedArray()
            val transactionId = parts[0]
            val transactionAmount = parts[1].toDouble()
            val transactionDate = DateConverter.getLong(parts[2])
            transactions.add(Transactions(transactionId, transactionAmount, transactionDate))
        }

        transactions.sortWith { o1, o2 -> o1.compareTo(o2) }

        return transactions
    }


    /**
     * Return array of specific size
     * */
    fun returnSpecificSize(n: Int, list: List<Pair<String, Int>>): MutableList<String> {
        val finalList = mutableListOf<String>()
        val pairedList = list.take(n)
        pairedList.map {
            finalList.add(it.first)
        }
        return finalList
    }


    /**
     * Returns list of key value pair
     * */
    fun pairOfSortedList(): List<Pair<String, Int>> {
        readAllLinesFromFile(CSV_PATH)
        val transactions: ArrayList<Transactions> = convertToTransactions(aList)
        val listOfTransactions = mutableListOf<Transactions>()
        for (transaction in transactions) {
            //add all transactions to list
            listOfTransactions.add(
                Transactions(
                    transaction.transactionId,
                    transaction.transactionAmount,
                    transaction.transactionDate
                )
            )
        }


        //empty Array to store unique elements
        val uniqueArraysElements = mutableListOf<String>()

        listOfTransactions.forEachIndexed { index, element ->
            if (element.transactionId == listOfTransactions[index].transactionId) {
                if (!uniqueArraysElements.contains(element.transactionId)) {
                    uniqueArraysElements.add(element.transactionId)
                }
            }
        }

        //for every item in unique array add its elements inside it and later compare consecutive days
        val individualArrays = mutableListOf<List<Transactions>>()

        uniqueArraysElements.forEachIndexed { index, _ ->
            listOfTransactions.forEachIndexed { _, transactionItem ->
                if (uniqueArraysElements[index] == transactionItem.transactionId) {
                    /**
                     * List will contain 3 items since they are the unique items(for this)
                     * though its dynamic (it will hold number of element equal to size)
                     * filter items
                     * */
                    val filteredList: List<Transactions> =
                        listOfTransactions.filter { t -> t.transactionId == uniqueArraysElements[index] }
                    if (individualArrays.isEmpty()) {
                        individualArrays.add(filteredList)
                    }
                } else {
                    val filteredList: List<Transactions> =
                        listOfTransactions.filter { t -> t.transactionId == uniqueArraysElements[index] }
                    if (!individualArrays.contains(filteredList)) {
                        individualArrays.add(filteredList)
                    }
                }
            }
        }

        /**
         * Check consecutive state of transaction in each day for each item
         * */
        //empty array to hold consecutive days for each element
        val consecutiveDaysArray = mutableListOf<Int>()

        individualArrays.forEachIndexed { index, transaction ->
            //sort list with dates
            val sortedList = transaction.sortedByDescending { t -> t.transactionDate }

            var totalConsecutiveDays = 0
            for (item in sortedList) {
                //compare dates here
                if ((sortedList.indexOf(item) + 1) < sortedList.size) {
                    val current = convertToDays(item.transactionDate)
                    val previous = convertToDays(sortedList[sortedList.indexOf(item) + 1].transactionDate)
                    val difference = current - previous
                    if (difference <= 1) {
                        totalConsecutiveDays++
                    }
                } else {
                    consecutiveDaysArray.add(totalConsecutiveDays)
                }
            }
        }

        println(uniqueArraysElements)
        println(consecutiveDaysArray)


        /**
         * Map unique elements with returned list(same size)
         * */
        val mapNumber = mutableMapOf<String, Int>()
            .apply { for (i in uniqueArraysElements.indices) this[uniqueArraysElements[i]] = consecutiveDaysArray[i] }


        /**
         * Sort list
         * */
        val mapList = mapNumber.toList()
        return mapList.sortedByDescending { t -> t.second }
    }
}