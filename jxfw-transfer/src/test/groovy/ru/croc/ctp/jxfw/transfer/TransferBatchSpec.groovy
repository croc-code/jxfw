package ru.croc.ctp.jxfw.transfer

import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.beans.factory.annotation.Autowired
import ru.croc.ctp.jxfw.transfer.impl.imp.ExecuteImportRequest
import ru.croc.ctp.jxfw.transfer.spec.BatchSpecification
import spock.lang.Ignore

import javax.batch.runtime.BatchStatus

/**
 * TODO JAVADOCS
 *
 * @author Nosov Alexander
 * @since
 */
class TransferBatchSpec extends BatchSpecification {

    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils

    @Autowired
    TransferService transferService

    @Ignore
    def "should pass"() {
        given:
        def request = new ExecuteImportRequest.ExecuteImportRequestBuilder()
                .scenarioName("importJob")
                .uploadedDataId(UUID.randomUUID())
                .build()
        when:
        transferService.executeImport(request)

        then:
        true
    }

    @Ignore
    def "should correct read data from xml"() {
        when:
        JobParameters jobParameters = new JobParametersBuilder()
                .addDate("creationDate", new Date())
                .addString("resourceId", "stub")
                .addString("operationId", "stub")
                .toJobParameters()

        def execution = jobLauncherTestUtils.launchStep("stepOneForImport", jobParameters)

        then:
        execution.status.batchStatus == BatchStatus.COMPLETED

    }
}