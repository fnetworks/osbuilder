package org.fnet.osbuilder.toolchain.repositories.utilities;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DirectoryListingParser {

	public static class RemoteFile {
		private String name;
		private URL url;

		private RemoteFile(String name, URL url) {
			this.name = name;
			this.url = url;
		}

		public String getName() {
			return name;
		}

		public URL getUrl() {
			return url;
		}
	}

	public static RemoteFile[] parse(URL url) throws IOException {
		Document doc = Jsoup.parse(url, 5000);
		Element table = doc.getElementsByTag("table").first();
		if (!table.getElementsByTag("tbody").isEmpty())
			table = table.getElementsByTag("tbody").first();
		Elements tableRows = table.getElementsByTag("tr");
		List<RemoteFile> directoryList = new ArrayList<>();
		for (Element row : tableRows) {
			Elements columns = row.getElementsByTag("td");
			if (columns.isEmpty())
				continue;
			Element link = columns.select("a").first();
			if (link == null)
				continue;
			String text = link.text();
			if (text.toLowerCase().contains("parent directory"))
				text = "..";
			directoryList.add(new RemoteFile(text, new URL(link.absUrl("href"))));
		}
		return directoryList.toArray(new RemoteFile[0]);
	}

}
