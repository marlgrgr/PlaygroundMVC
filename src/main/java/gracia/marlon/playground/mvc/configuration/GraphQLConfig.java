package gracia.marlon.playground.mvc.configuration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;

@Configuration
public class GraphQLConfig {

	@Bean
	GraphQLScalarType utilDateScalar() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

		return GraphQLScalarType.newScalar().name("Date").description("java.util.Date scalar")
				.coercing(new Coercing<Date, String>() {
					@Override
					public String serialize(Object dataFetcherResult) {
						if (dataFetcherResult instanceof Date) {
							return sdf.format((Date) dataFetcherResult);
						}
						throw new CoercingSerializeException("Expected a Date object.");
					}

					@Override
					public Date parseValue(Object input) {
						try {
							if (input instanceof String) {
								return sdf.parse((String) input);
							}
						} catch (ParseException e) {
							throw new CoercingParseValueException("Invalid date format", e);
						}
						throw new CoercingParseValueException("Expected a String");
					}

					@Override
					public Date parseLiteral(Object input) {
						if (input instanceof StringValue) {
							try {
								return sdf.parse(((StringValue) input).getValue());
							} catch (ParseException e) {
								throw new CoercingParseLiteralException("Invalid date format", e);
							}
						}
						throw new CoercingParseLiteralException("Expected a StringValue");
					}
				}).build();
	}

	@Bean
	RuntimeWiringConfigurer runtimeWiringConfigurer() {
		return wiringBuilder -> wiringBuilder.scalar(utilDateScalar());
	}
}
