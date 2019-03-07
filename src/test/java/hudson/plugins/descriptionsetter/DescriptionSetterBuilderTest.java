package hudson.plugins.descriptionsetter;

import hudson.model.FreeStyleBuild;
import hudson.model.Result;
import hudson.model.FreeStyleProject;

import org.jvnet.hudson.test.HudsonTestCase;

public class DescriptionSetterBuilderTest extends HudsonTestCase {

	public void testSuccessDefaultDescription() throws Exception {
		assertEquals("one",
				getDescription("text one", Result.SUCCESS, "text (.*)", null, false));
	}

	public void testSuccessDefaultDescriptionMultiLine() throws Exception {
		assertEquals("line one\nline two",
				getDescription("<test>\nline one\nline two\n</test>", Result.SUCCESS, "(?s)<test>\n(.*)\n<\\/test>", null, true));
	}

	public void testSuccessConfiguredDescription() throws Exception {
		assertEquals(
				"description one",
				getDescription("text one", Result.SUCCESS, "text (.*)",
						"description \\1", false));
	}

	public void testSuccessConfiguredDescription2() throws Exception {
		assertEquals(
				"description one two",
				getDescription("text one two", Result.SUCCESS,
						"text (\\w+) (\\w+)", "description \\1 \\2", false));
	}

	public void testFailureWithNoFailureRegex() throws Exception {
		assertEquals("one",
				getDescription("text one", Result.FAILURE, "text (.*)", null, false));
	}

	public void testSuccessWithFixedDescription() throws Exception {
		assertEquals(
				"description success",
				getDescription("xxx", Result.SUCCESS, null,
						"description success", false));
	}

	public void testSuccessNoMatch() throws Exception {
		assertEquals(
				null,
				getDescription("xxx", Result.SUCCESS, "regex",
						"description success", false));
	}

	public void testURL() throws Exception {
		assertEquals(
				"<a href=\"http://foo/bar\">http://foo/bar</a>",
				getDescription("url:http://foo/bar", Result.SUCCESS,
						"url:(.*)", null, false));
	}

	public void testNullMatch1() throws Exception {
		assertEquals(
				"Match=(MatchOne) MatchTwo",
				getDescription("Prefix: MatchOne MatchTwo", Result.SUCCESS,
						"^Prefix: (\\S+)( .*)?$", "Match=(\\1)\\2", false));
	}

	public void testNullMatch2() throws Exception {
		assertEquals(
				"Match=(MatchOne)",
				getDescription("Prefix: MatchOne", Result.SUCCESS,
						"^Prefix: (\\S+)( .*)?$", "Match=(\\1)\\2", false));
	}

	private String getDescription(String text, Result result, String regexp,
			String description, boolean useMultiLine) throws Exception {
		FreeStyleProject project = createFreeStyleProject();
		project.getBuildersList().add(new TestBuilder(text, result));
		project.getBuildersList().add(
				new DescriptionSetterBuilder(regexp, description, useMultiLine));
		FreeStyleBuild build = project.scheduleBuild2(0).get();
		return build.getDescription();
	}

}
