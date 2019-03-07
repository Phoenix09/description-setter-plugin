package hudson.plugins.descriptionsetter;

import hudson.model.FreeStyleBuild;
import hudson.model.Result;
import hudson.model.FreeStyleProject;

import org.jvnet.hudson.test.HudsonTestCase;

public class DescriptionSetterPublisherTest extends HudsonTestCase {

	public void testSuccessDefaultDescription() throws Exception {
		assertEquals("one", getDescription("text one", Result.SUCCESS,
				"text (.*)", null, null, null, false));
	}

	public void testSuccessDefaultDescriptionMultiLine() throws Exception {
		assertEquals("line one\nline two",
				getDescription("<test>\nline one\nline two\n</test>", Result.SUCCESS,
				"(?s)<test>\n(.*)\n<\\/test>", null, null, null, true));
	}

	public void testSuccessConfiguredDescription() throws Exception {
		assertEquals("description one", getDescription("text one",
				Result.SUCCESS, "text (.*)", null, "description \\1", null, false));
	}

        public void testSuccessConfiguredDescription2() throws Exception {
		assertEquals("description one two", getDescription("text one two",
				Result.SUCCESS, "text (\\w+) (\\w+)", null, "description \\1 \\2", null, false));
        }

	public void testFailureWithNoFailureRegex() throws Exception {
		assertEquals("one", getDescription("text one", Result.FAILURE,
				"text (.*)", null, null, null, false));
	}

	public void testFailureWithFailureRegexAndDefaultDescrption() throws Exception {
		assertEquals("text", getDescription("text one", Result.FAILURE,
				"text (.*)", "(.*) one", null, null, false));
	}

	public void testFailureWithFailureRegexAndConfiguredDescription() throws Exception {
		assertEquals("description text", getDescription("text one",
				Result.FAILURE, "text (.*)", "(.*) one", null,
				"description \\1", false));
	}

	public void testSuccessWithFixedDescription() throws Exception {
		assertEquals("description success", getDescription("xxx",
				Result.SUCCESS, null, null, "description success",
				"description failure", false));
	}

	public void testFailureWithFixedDescription() throws Exception {
		assertEquals("description failure", getDescription("xxx",
				Result.FAILURE, null, null, "description success",
				"description failure", false));
	}

	public void testSuccessNoMatch() throws Exception {
		assertEquals(null, getDescription("xxx",
				Result.SUCCESS, "regex", null, "description success",
				null, false));
	}

	public void testURL() throws Exception {
		assertEquals("<a href=\"http://foo/bar\">http://foo/bar</a>", getDescription("url:http://foo/bar",
				Result.SUCCESS, "url:(.*)", null, null,
				null, false));
	}
	
	public void testNullMatch1() throws Exception {
		assertEquals("Match=(MatchOne) MatchTwo",
				getDescription("Prefix: MatchOne MatchTwo", Result.SUCCESS,
						"^Prefix: (\\S+)( .*)?$", null, 
						"Match=(\\1)\\2", null, false));
	}

	public void testNullMatch2() throws Exception {
		assertEquals("Match=(MatchOne)",
				getDescription("Prefix: MatchOne", Result.SUCCESS,
						"^Prefix: (\\S+)( .*)?$", null,
						"Match=(\\1)\\2", null, false));
	}

	private String getDescription(String text, Result result, String regexp,
			String regexpForFailed, String description,
			String descriptionForFailed, boolean useMultiLine) throws Exception {
		FreeStyleProject project = createFreeStyleProject();
		project.getBuildersList().add(new TestBuilder(text, result));
		project.getPublishersList().add(
				new DescriptionSetterPublisher(regexp, regexpForFailed,
						description, descriptionForFailed, false, useMultiLine));
		FreeStyleBuild build = project.scheduleBuild2(0).get();
		return build.getDescription();
	}

}
