package gracia.marlon.playground.mvc.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.language.IntValue;
import graphql.language.StringValue;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;

public class GraphQLConfigTest {

	@Test
	public void utilDateScalarSuccessfullTest() {
		Date testDate = new Date(1748202841000L);

		GraphQLContext graphQLContext = Mockito.mock(GraphQLContext.class);
		Locale locale = Mockito.mock(Locale.class);
		CoercedVariables coercedVariables = Mockito.mock(CoercedVariables.class);

		GraphQLConfig graphQLConfig = new GraphQLConfig();
		GraphQLScalarType graphQLScalarType = graphQLConfig.utilDateScalar();

		String result = (String) graphQLScalarType.getCoercing().serialize(testDate, graphQLContext, locale);

		assertEquals("2025-05-25T14:54:01.000-05:00", result);

		assertThrows(CoercingSerializeException.class,
				() -> graphQLScalarType.getCoercing().serialize(new Object(), graphQLContext, locale));

		Date resultDate = (Date) graphQLScalarType.getCoercing().parseValue(result, graphQLContext, locale);
		assertEquals(1748202841000L, resultDate.getTime());

		assertThrows(CoercingParseValueException.class,
				() -> graphQLScalarType.getCoercing().parseValue("bad format", graphQLContext, locale));

		assertThrows(CoercingParseValueException.class,
				() -> graphQLScalarType.getCoercing().parseValue(new Object(), graphQLContext, locale));

		StringValue stringValue = Mockito.mock(StringValue.class);
		Mockito.when(stringValue.getValue()).thenReturn(result).thenReturn("bad format");

		IntValue intValue = Mockito.mock(IntValue.class);

		resultDate = (Date) graphQLScalarType.getCoercing().parseLiteral(stringValue, coercedVariables, graphQLContext,
				locale);
		assertEquals(1748202841000L, resultDate.getTime());

		assertThrows(CoercingParseLiteralException.class, () -> graphQLScalarType.getCoercing()
				.parseLiteral(stringValue, coercedVariables, graphQLContext, locale));

		assertThrows(CoercingParseLiteralException.class,
				() -> graphQLScalarType.getCoercing().parseLiteral(intValue, coercedVariables, graphQLContext, locale));

	}
}
