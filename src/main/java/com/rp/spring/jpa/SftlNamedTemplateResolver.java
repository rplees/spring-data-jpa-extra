package com.rp.spring.jpa;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.Resource;

/**
 * .
 *
 * @author stormning on 2016/12/17.
 */
public class SftlNamedTemplateResolver implements NamedTemplateResolver {

	private String encoding = "UTF-8";

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	@Override
	public Iterator<Void> doInTemplateResource(Resource resource, final NamedTemplateCallback callback)
			throws Exception {
		InputStream inputStream = resource.getInputStream();
		final List<String> lines = IOUtils.readLines(inputStream, encoding);
		return new Iterator<Void>() {
			String name;
			StringBuilder content = new StringBuilder();
			int index = 0;
			int total = lines.size();

			@Override
			public boolean hasNext() {
				return index < total;
			}

			@Override
			public Void next() {
				do {
					String line = lines.get(index);
					if (isNameLine(line)) {
						name = StringUtils.trim(StringUtils.remove(line, "--"));
					}
					else {
						line = StringUtils.trimToNull(line);
						if (line != null) {
							content.append(line).append(" ");
						}
					}
					index++;
				} while (!isLastLine() && !isNextNameLine());

				//next template
				callback.process(name, content.toString());
				name = null;
				content = new StringBuilder();
				return null;
			}

			@Override
			public void remove() {
				//ignore
			}

			private boolean isNameLine(String line) {
				return StringUtils.startsWith(StringUtils.trim(line), "--");
			}

			private boolean isNextNameLine() {
				String line = lines.get(index);
				return isNameLine(line);
			}

			private boolean isLastLine() {
				return index == total;
			}
		};
	}
}
