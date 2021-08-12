package ru.croc.ctp.jxfw.transfer

import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobInstance
import org.springframework.batch.core.explore.JobExplorer
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.beans.factory.annotation.Autowired
import ru.croc.ctp.jxfw.transfer.impl.TransferServiceImpl
import ru.croc.ctp.jxfw.transfer.spec.BatchSpecification
import spock.lang.Ignore

/**
 *
 * @author Nosov Alexander
 * @since 1.4
 */
class TransferServiceSpec extends BatchSpecification {
    
    @Autowired
    TransferService transferService
    
    def jobExplorer = Mock(JobExplorer)
    def jobLauncher = Mock(JobLauncher)

    def setup() {
        (transferService as TransferServiceImpl).setJobExplorer(jobExplorer)
        (transferService as TransferServiceImpl).setJobLauncher(jobLauncher)
    }
    
    def "test executeImport"() {
        
    }

    def "test executeExport"() {
        
    }

    @Ignore
    def "test operations"() {
        given:
        jobExplorer.getJobNames() >> ["1", "2"]
        def jobInstance1 = new JobInstance(1, "1")
        jobExplorer.getJobInstances("1", 0, 256) >> [jobInstance1]
        def jobInstance2 = new JobInstance(2, "2")
        jobExplorer.getJobInstances("2", 0, 256) >> [jobInstance2]
        def execution1 = new JobExecution(jobInstance1, null, null)
        execution1.setStatus(BatchStatus.STARTED)
        def execution2 = new JobExecution(jobInstance2, null, null)
        execution2.setStatus(BatchStatus.FAILED)
        jobExplorer.getJobExecutions(jobInstance1) >> [execution1] 
        jobExplorer.getJobExecutions(jobInstance2) >> [execution2]
        
        when:
        def result = transferService.operations()
        
        then:
        result.size() == 1
    }

}
