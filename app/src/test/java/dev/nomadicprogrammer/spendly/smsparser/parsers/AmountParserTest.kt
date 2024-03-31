package dev.nomadicprogrammer.spendly.smsparser.parsers

import org.junit.Assert.assertEquals
import org.junit.Test

class AmountParserTest{
    private val testInputs = listOf(
        "You have spent Ksh 1000.00 at Naivas on 2021-09-01",
        "You have spent Ksh Rs 1000.00 at Naivas on 2021-09-01",
        "You have spent Ksh rs 1000.0 at Naivas on 2021-09-01",
        "You have spent Ksh INR 1000.0 at Naivas on 2021-09-01",
        "You have spent Ksh iNR 1000.0 at Naivas on 2021-09-01",
        "You have spent Ksh inR 1000.0 at Naivas on 2021-09-01",
        "You have spent Ksh inr 1000.0 at Naivas on 2021-09-01",
        "You have spent Ksh iNr 1000.0 at Naivas on 2021-09-01",
        "You have spent Ksh INr 1000.0 at Naivas on 2021-09-01",
        "You have spent Ksh inR 1000.0 at Naivas on 2021-09-01",
        "You have spent Ksh RS 10000.0 at Naivas on 2021-09-01",
        "You have spent Ksh $ 10000.0 at Naivas on 2021-09-01",
        "You have spent Ksh ₹ 10000.0 at Naivas on 2021-09-01",
    )

    private val expectedOutputs = listOf(
        "1000.00",
        "Rs 1000.00",
        "rs 1000.0",
        "INR 1000.0",
        "iNR 1000.0",
        "inR 1000.0",
        "inr 1000.0",
        "iNr 1000.0",
        "INr 1000.0",
        "inR 1000.0",
        "RS 10000.0",
        "$ 10000.0",
        "₹ 10000.0"
    )
    @Test
    fun testVariousTypesOfAmountPresentInText(){
        val parser = AmountParser()

        val observedOutputs = mutableListOf<String?>()
        testInputs.mapTo(observedOutputs) { text ->
            parser.parse(text)
        }

        assertEquals(expectedOutputs, observedOutputs)
    }
}