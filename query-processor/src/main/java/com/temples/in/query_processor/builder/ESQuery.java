package com.temples.in.query_processor.builder;

public class ESQuery {
	private String path;

	public static class Builder {
		private String path = "";

		public Builder path(String path) {
			if (this.path.length() > 0) {
				this.path = this.path + "/" + path;
			} else {
				this.path = path;
			}
			return this;
		}

		public String build() {
			return new ESQuery(this).path;
		}

	}

	public ESQuery(Builder builder) {
		this.path = builder.path;
	}
}