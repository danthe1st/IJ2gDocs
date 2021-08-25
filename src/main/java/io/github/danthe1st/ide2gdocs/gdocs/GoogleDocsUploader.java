package io.github.danthe1st.ide2gdocs.gdocs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.function.Consumer;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets.Details;
import com.google.api.services.docs.v1.Docs;
import com.google.api.services.docs.v1.model.BatchUpdateDocumentRequest;
import com.google.api.services.docs.v1.model.BatchUpdateDocumentResponse;
import com.google.api.services.docs.v1.model.DeleteContentRangeRequest;
import com.google.api.services.docs.v1.model.Document;
import com.google.api.services.docs.v1.model.InsertTextRequest;
import com.google.api.services.docs.v1.model.Location;
import com.google.api.services.docs.v1.model.Range;
import com.google.api.services.docs.v1.model.Request;
import com.google.api.services.docs.v1.model.Response;
import com.google.api.services.docs.v1.model.StructuralElement;

public class GoogleDocsUploader {

	private static final int MAX_QUEUE_SIZE = 10;//TODO make this configurable

	private final BlockingQueue <Runnable> workQueue = new LinkedBlockingQueue <>();
	private final ExecutorService threadPool = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, workQueue);//important: just a single thread
	private final Docs service;
	private Document doc;
	private int startIndex;
	private int lastLen;
	private int lastHashCode = -1;
	private volatile boolean dirty;


	public GoogleDocsUploader(Docs service) {
		this.service = service;
	}

	public static Details getCredentialDetails(String clientId, String clientSecret) {
		Details details = new Details();
		details.setClientId(clientId);
		details.setClientSecret(clientSecret);
		return details;
	}

	public synchronized void setDocument(String id) throws IOException {
		//TODO fix thread safety with actual worker functions
		doc = service.documents().get(id).execute();
		startIndex = getLastIndex() - 1;
		lastLen = 0;
		lastHashCode = -1;
	}

	public synchronized void overwritePart(String newPart, int offset, int oldLen, String fullText, Consumer <IOException> exceptionHandler) {
		if(dirty || workQueue.size() >= MAX_QUEUE_SIZE) {
			overwriteEverything(fullText, exceptionHandler);
		} else {
			threadPool.submit(() -> {
				try {
					actuallyOverwritePart(newPart, offset, oldLen, fullText.hashCode());
				} catch(IOException e) {
					exceptionHandler.accept(e);
				}
			});
		}
	}

	public synchronized void overwriteEverything(String newText, Consumer <IOException> exceptionHandler) {
		workQueue.clear();
		threadPool.submit(() -> {
			try {
				actuallyOverwriteEverything(newText);
			} catch(IOException e) {
				exceptionHandler.accept(e);
			}
		});
	}

	private void actuallyOverwritePart(String newPart, int offset, int oldPartLen, int newFullTextHashCode) throws IOException {

		if(lastHashCode == newFullTextHashCode) {
			return;
		}
		int firstIndex = startIndex + offset;
		int lastIndex = startIndex + oldPartLen + offset - 1;
		List <Request> req = buildOverwriteRequests(newPart, firstIndex, lastIndex);

		if(!req.isEmpty()) {
			try {
				executeMultiple(req);
				lastLen += countLengthWithoutWindowsLineBreaks(newPart) - oldPartLen;
				lastHashCode = newFullTextHashCode;
			} catch(IOException e) {
				dirty = true;
				lastHashCode = -1;
				throw e;
			}

		}
	}

	private List <Request> buildOverwriteRequests(String newPart, int firstIndex, int lastIndex) {
		List <Request> req = new ArrayList <>();
		if(lastIndex >= firstIndex) {
			req.add(new Request().setDeleteContentRange(
					new DeleteContentRangeRequest().setRange(new Range().setStartIndex(firstIndex).setEndIndex(lastIndex+1))));
		}
		if(!newPart.isEmpty()) {
			req.add(new Request().setInsertText(new InsertTextRequest().setText(newPart).setLocation(new Location().setIndex(firstIndex))));
		}
		return req;
	}

	private void actuallyOverwriteEverything(String newText) throws IOException {
		try {
			int hashCode = newText.hashCode();
			if(lastHashCode == hashCode) {
				return;
			}
			int firstIndex = startIndex;
			int lastIndex = startIndex + lastLen - 1;
			List <Request> req = buildOverwriteRequests(newText, firstIndex, lastIndex);
			if(!req.isEmpty()) {
				executeMultiple(req);
				lastLen = countLengthWithoutWindowsLineBreaks(newText);
				lastHashCode = hashCode;
				dirty = false;
			}
		} catch(IOException e) {
			doc = service.documents().get(doc.getDocumentId()).execute();
			startIndex = Math.min(startIndex, getLastIndex());
			lastLen = getLastIndex() - startIndex;
			lastHashCode = -1;
			dirty = true;
			throw e;
		}
	}

	private static int countLengthWithoutWindowsLineBreaks(String toCount) {
		char[] chars = toCount.toCharArray();
		char lastChar = '\0';
		int ret = 0;
		for(int i = 0; i < chars.length; i++) {
			if(lastChar != '\r' || chars[i] != '\n') {
				ret++;
			}
			lastChar = chars[i];
		}
		return ret;
	}

	private int getLastIndex() {
		List <StructuralElement> content = doc.getBody().getContent();
		return content.get(content.size() - 1).getEndIndex();
	}

	private List <Response> executeMultiple(List <Request> requests) throws IOException {
		BatchUpdateDocumentRequest body = new BatchUpdateDocumentRequest().setRequests(requests);

		BatchUpdateDocumentResponse response = service.documents().batchUpdate(doc.getDocumentId(), body).execute();
		return response.getReplies();
	}
}
