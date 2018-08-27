package org.archivemanager.server.web.model;
import java.io.IOException;
import org.heed.openapps.QName;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class JacksonQNameModule extends SimpleModule {
	private static final long serialVersionUID = 2474538804885591910L;

	
	public JacksonQNameModule() {
		addSerializer(QName.class, new QNameSerializer());
	}
	
	public static class QNameSerializer extends StdSerializer<QName> {
		private static final long serialVersionUID = -4107066628968332381L;
		
		public QNameSerializer() {
			this(null);
		}
		protected QNameSerializer(Class<QName> vc) {
			super(vc);
		}		
		@Override
		public void serialize(QName qname, JsonGenerator jgen, SerializerProvider provider) throws IOException {
			jgen.writeString(qname.toString());
		}
		
	}
}
