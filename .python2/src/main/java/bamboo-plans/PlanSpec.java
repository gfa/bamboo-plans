package bambooPlans;

import com.atlassian.bamboo.specs.api.BambooSpec;
import com.atlassian.bamboo.specs.api.builders.BambooKey;
import com.atlassian.bamboo.specs.api.builders.BambooOid;
import com.atlassian.bamboo.specs.api.builders.credentials.SharedCredentialsIdentifier;
import com.atlassian.bamboo.specs.api.builders.permission.PermissionType;
import com.atlassian.bamboo.specs.api.builders.permission.Permissions;
import com.atlassian.bamboo.specs.api.builders.permission.PlanPermissions;
import com.atlassian.bamboo.specs.api.builders.plan.Job;
import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.api.builders.plan.PlanIdentifier;
import com.atlassian.bamboo.specs.api.builders.plan.Stage;
import com.atlassian.bamboo.specs.api.builders.plan.branches.BranchCleanup;
import com.atlassian.bamboo.specs.api.builders.plan.branches.PlanBranchManagement;
import com.atlassian.bamboo.specs.api.builders.plan.configuration.AllOtherPluginsConfiguration;
import com.atlassian.bamboo.specs.api.builders.plan.configuration.ConcurrentBuilds;
import com.atlassian.bamboo.specs.api.builders.plan.dependencies.Dependencies;
import com.atlassian.bamboo.specs.api.builders.plan.dependencies.DependenciesConfiguration;
import com.atlassian.bamboo.specs.api.builders.project.Project;
import com.atlassian.bamboo.specs.api.model.BambooKeyProperties;
import com.atlassian.bamboo.specs.api.model.BambooOidProperties;
import com.atlassian.bamboo.specs.api.model.plan.JobProperties;
import com.atlassian.bamboo.specs.api.model.plan.PlanProperties;
import com.atlassian.bamboo.specs.api.model.plan.StageProperties;
import com.atlassian.bamboo.specs.api.model.plan.branches.PlanBranchManagementProperties;
import com.atlassian.bamboo.specs.api.model.project.ProjectProperties;
import com.atlassian.bamboo.specs.builders.repository.git.GitRepository;
import com.atlassian.bamboo.specs.builders.task.CheckoutItem;
import com.atlassian.bamboo.specs.builders.task.MavenTask;
import com.atlassian.bamboo.specs.builders.task.ScriptTask;
import com.atlassian.bamboo.specs.builders.task.VcsCheckoutTask;
import com.atlassian.bamboo.specs.builders.trigger.RepositoryPollingTrigger;
import com.atlassian.bamboo.specs.model.task.MavenTaskProperties;
import com.atlassian.bamboo.specs.model.task.VcsCheckoutTaskProperties;
import com.atlassian.bamboo.specs.model.trigger.RepositoryPollingTriggerProperties;
import com.atlassian.bamboo.specs.util.BambooServer;
import com.atlassian.bamboo.specs.util.MapBuilder;


/**
 * Plan configuration for Bamboo.
 * Learn more on: <a href="https://confluence.atlassian.com/display/BAMBOO/Bamboo+Specs">https://confluence.atlassian.com/display/BAMBOO/Bamboo+Specs</a>
 */
@BambooSpec
public class PlanSpec {

    /**
     * Run main to publish plan on Bamboo
     */
    public static void main(final String[] args) throws Exception {
        //By default credentials are read from the '.credentials' file.
        BambooServer bambooServer = new BambooServer("http://localhost:8085");

        Plan plan = new PlanSpec().createPlan();

        bambooServer.publish(plan);
    }

    Project project() {
        return new Project()
                .name("PROJECT")
                .key("PR");
    }

    Plan createPlan() {
    return new Plan(project(), "<project name>", "<project initials>")
	    .planRepositories(new GitRepository()
			 .name("main")
			 .url("<url>")
			 .branch("master|develop")
			 .authentication(new SharedCredentialsIdentifier("SSHkey")))
            .description("This plan tests <project name>")
            .stages(new Stage("Stage 1")
                         .jobs(new Job("Default Job",
                                new BambooKey("JOB1"))
                         .pluginConfigurations(new AllOtherPluginsConfiguration()
                         .configuration(new MapBuilder()
                                  .put("repositoryDefiningWorkingDirectory", -1)
                                  .build()))
                         .tasks(new VcsCheckoutTask()
                                  .description("Checkout Default Repository")
                                  .checkoutItems(new CheckoutItem().defaultRepository())
                                  .cleanCheckout(true),
                                new ScriptTask()
                                  .description("Run python tests")
                                  .inlineBody("python2 setup.py test"))))
            .triggers(new RepositoryPollingTrigger()
                         .name("Repository polling"))
            .planBranchManagement(new PlanBranchManagement()
		    .createForVcsBranch()
                    .delete(new BranchCleanup()
                        .whenRemovedFromRepositoryAfterDays(7)
                        .whenInactiveInRepositoryAfterDays(30))
                    .notificationForCommitters()
                    .issueLinkingEnabled(false));
	}
}
