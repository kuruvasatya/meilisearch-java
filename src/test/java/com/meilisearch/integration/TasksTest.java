package com.meilisearch.integration;

import static org.junit.jupiter.api.Assertions.*;

import com.meilisearch.integration.classes.AbstractIT;
import com.meilisearch.integration.classes.TestData;
import com.meilisearch.sdk.Index;
import com.meilisearch.sdk.Task;
import com.meilisearch.sdk.utils.Movie;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("integration")
public class TasksTest extends AbstractIT {

	private TestData<Movie> testData;

	@BeforeEach
	public void initialize() {
		this.setUp();
		if (testData == null) testData = this.getTestData(MOVIES_INDEX, Movie.class);
	}

	@AfterAll
	static void cleanMeiliSearch() {
		cleanup();
	}

	/** Test Get Task */
	@Test
	public void testClientGetTask() throws Exception {
		String indexUid = "GetClientTask";
		Task response = client.createIndex(indexUid);
		client.waitForTask(response.getUid());

		Task task = client.getTask(response.getUid());

		assertTrue(task instanceof Task);
		assertNotNull(task.getStatus());
		assertNotEquals("", task.getStatus());
		assertNotNull(task.getStartedAt());
		assertNotNull(task.getEnqueuedAt());
		assertNotNull(task.getFinishedAt());
		assertTrue(task.getUid() >= 0);
		assertNotNull(task.getDetails());
		if (task.getType() == "indexDeletion") {
			assertNotNull(task.getDetails().getDeletedDocuments());
		}
		if (task.getType() == "documentAddition") {
			assertNotNull(task.getDetails().getReceivedDocuments());
			assertNotNull(task.getDetails().getIndexedDocuments());
		}
	}

	/** Test Get Tasks */
	@Test
	public void testClientGetTasks() throws Exception {
		Task[] tasks = client.getTasks();

		for (Task task : tasks) {
			client.waitForTask(task.getUid());

			assertNotNull(task.getStatus());
			assertNotEquals("", task.getStatus());
			assertTrue(task.getUid() >= 0);
			assertNotNull(task.getDetails());
			if (task.getType() == "indexDeletion") {
				assertNotNull(task.getDetails().getDeletedDocuments());
			}
			if (task.getType() == "documentAddition") {
				assertNotNull(task.getDetails().getReceivedDocuments());
				assertNotNull(task.getDetails().getIndexedDocuments());
			}
		}
	}

	/** Test waitForTask */
	@Test
	public void testWaitForTask() throws Exception {
		String indexUid = "WaitForTask";
		Task response = client.createIndex(indexUid);
		client.waitForTask(response.getUid());

		Task task = client.getTask(response.getUid());

		assertTrue(task.getUid() >= 0);
		assertNotNull(task.getEnqueuedAt());
		assertNotNull(task.getStartedAt());
		assertNotNull(task.getFinishedAt());
		assertNotNull(task.getDetails());
		if (task.getType() == "indexDeletion") {
			assertNotNull(task.getDetails().getDeletedDocuments());
		}
		if (task.getType() == "documentAddition") {
			assertNotNull(task.getDetails().getReceivedDocuments());
			assertNotNull(task.getDetails().getIndexedDocuments());
		}
		assertEquals("succeeded", task.getStatus());

		client.deleteIndex(task.getIndexUid());
	}

	/** Test waitForTask timeoutInMs */
//    @Test
//    public void testWaitForTaskTimoutInMs() throws Exception {
//        String indexUid = "WaitForTaskTimoutInMs";
//        Index index = client.index(indexUid);
//
//        Task task = index.addDocuments(this.testData.getRaw());
//        index.waitForTask(task.getUid());
//
//        assertThrows(Exception.class, () -> index.waitForTask(task.getUid(), 0, 50));
//    }
}
