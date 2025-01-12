package io.reflectoring.diffparser.unified;

import io.reflectoring.diffparser.api.DiffParser;
import io.reflectoring.diffparser.api.UnifiedDiffParser;
import io.reflectoring.diffparser.api.model.Hunk;
import junit.framework.Assert;
import org.testng.annotations.Test;
import io.reflectoring.diffparser.api.model.Diff;
import io.reflectoring.diffparser.api.model.Line;

import java.io.InputStream;
import java.util.List;

/**
 * Tests the diffparser with a diff created by Tortoise SVN.
 */
@SuppressWarnings("deprecation")
public class TortoiseDiffTest {

    @Test
    public void testParse() throws Exception {
        // given
        DiffParser parser = new UnifiedDiffParser();
        InputStream in = getClass().getResourceAsStream("tortoise.diff");

        // when
        List<Diff> diffs = parser.parse(in);

        // then
        Assert.assertNotNull(diffs);
        Assert.assertEquals(2, diffs.size());

        Diff diff1 = diffs.get(0);
        Assert.assertEquals("/trunk/test1 - Kopie (2).txt", diff1.getFromFileName());
        Assert.assertEquals("/trunk/test1 - Kopie (2).txt", diff1.getToFileName());
        Assert.assertEquals(2, diff1.getHunks().size());

        List<String> headerLines = diff1.getHeaderLines();
        Assert.assertEquals(2, headerLines.size());

        Hunk hunk1 = diff1.getHunks().get(0);
        Assert.assertEquals(1, hunk1.getFromFileRange().getLineStart());
        Assert.assertEquals(4, hunk1.getFromFileRange().getLineCount());
        Assert.assertEquals(1, hunk1.getToFileRange().getLineStart());
        Assert.assertEquals(3, hunk1.getToFileRange().getLineCount());

        List<Line> lines = hunk1.getLines();
        Assert.assertEquals(6, lines.size());
        Assert.assertEquals(Line.LineType.NEUTRAL, lines.get(0).getLineType());
        Assert.assertEquals(Line.LineType.FROM, lines.get(1).getLineType());
        Assert.assertEquals(Line.LineType.TO, lines.get(2).getLineType());
        Assert.assertEquals(Line.LineType.NEUTRAL, lines.get(3).getLineType());
        Assert.assertEquals(Line.LineType.FROM, lines.get(4).getLineType());
        Assert.assertEquals(Line.LineType.NEUTRAL, lines.get(5).getLineType());

    }

    @Test
    public void testParse_WhenHunkRangeLineCountNotSpecified_ShouldSetHunkRangeLineCountToOne() throws Exception {
        // given
        DiffParser parser = new UnifiedDiffParser();
        String in = ""
            + "--- from	2015-12-21 17:53:29.082877088 -0500\n"
            + "+++ to	2015-12-21 08:41:52.663714666 -0500\n"
            + "@@ -10 +10 @@\n"
            + "-from\n"
            + "+to\n"
            + "\n";

        // when
        List<Diff> diffs = parser.parse(in.getBytes());

        // then
        Assert.assertNotNull(diffs);
        Assert.assertEquals(1, diffs.size());

        Diff diff1 = diffs.get(0);
        Assert.assertEquals(1, diff1.getHunks().size());

        Hunk hunk1 = diff1.getHunks().get(0);
        Assert.assertEquals(1, hunk1.getFromFileRange().getLineCount());
        Assert.assertEquals(1, hunk1.getToFileRange().getLineCount());
    }

    @Test
    public void testParse_WhenInputDoesNotEndWithEmptyLine_ShouldTransitionToEndState() throws Exception {
        // given
        DiffParser parser = new UnifiedDiffParser();
        String in = ""
            + "--- from	2015-12-21 17:53:29.082877088 -0500\n"
            + "+++ to	2015-12-21 08:41:52.663714666 -0500\n"
            + "@@ -10,1 +10,1 @@\n"
            + "-from\n"
            + "+to\n";

        // when
        List<Diff> diffs = parser.parse(in.getBytes());

        // then
        Assert.assertNotNull(diffs);
        Assert.assertEquals(1, diffs.size());

        Diff diff1 = diffs.get(0);
        Assert.assertEquals(1, diff1.getHunks().size());
    }
}
