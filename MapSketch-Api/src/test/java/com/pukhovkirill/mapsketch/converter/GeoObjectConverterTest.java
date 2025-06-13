package com.pukhovkirill.mapsketch.converter;

import java.util.*;
import java.text.NumberFormat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.pukhovkirill.mapsketch.dto.Geometry;
import com.pukhovkirill.mapsketch.entity.GeoObject;
import com.pukhovkirill.mapsketch.dto.GeoObjectPayload;

class GeoObjectConverterTest {

    static final int MAX_FRACTION_DIGIT = 15;
    static final NumberFormat nf;

    static{
        nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(MAX_FRACTION_DIGIT);
    }

    GeoObjectConverter converter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        converter = new GeoObjectConverter();
    }

    @Test
    void testConvertFromEntityToDto_withPointGeometry(){
        GeoObject entity = mock(GeoObject.class);

        final var id = 1L;
        final var name = "test1";
        final var type = GeoObject.GeoObjectType.POINT;

        final var rnd = new Random();

        double lontitude = rnd.nextDouble(25);
        double lattitude = rnd.nextDouble(25);

        final var coordinates = String.format(
                "[%s,%s]",
                nf.format(lontitude),
                nf.format(lattitude)
        );

        when(entity.id()).thenReturn(id);
        when(entity.name()).thenReturn(name);
        when(entity.type()).thenReturn(type);
        when(entity.coordinates()).thenReturn(coordinates);

        var res = converter.convert(entity);

        assertNotNull(res);
        assertEquals(res.getClass(), GeoObjectPayload.class);

        assertEquals("Feature", res.getType());

        assertTrue(res.getProperties().containsKey("id"));
        assertEquals(id, (Long)res.getProperties().get("id"));

        assertTrue(res.getProperties().containsKey("name"));
        assertEquals(name, res.getProperties().get("name"));

        assertEquals(type.toString(), res.getGeometry().getType());

        List<?> items = res.getGeometry().getCoordinates();
        assertEquals(0, (int)((lontitude - (double) items.getFirst()) * 1000000));
        assertEquals(0, (int)((lattitude - (double) items.getLast()) * 1000000));
    }

    @Test
    void testConvertFromEntityToDto_withLineGeometry(){
        GeoObject entity = mock(GeoObject.class);

        final var id = 2L;
        final var name = "test2";
        final var type = GeoObject.GeoObjectType.LINE_STRING;

        final var rnd = new Random();

        double p1lontitude = rnd.nextDouble(25);
        double p1lattitude = rnd.nextDouble(25);

        double p2lontitude = rnd.nextDouble(25);
        double p2lattitude = rnd.nextDouble(25);

        final var coordinates = String.format(
                "[[%s,%s],[%s,%s]]",
                nf.format(p1lontitude),
                nf.format(p1lattitude),
                nf.format(p2lontitude),
                nf.format(p2lattitude)
        );

        when(entity.id()).thenReturn(id);
        when(entity.name()).thenReturn(name);
        when(entity.type()).thenReturn(type);
        when(entity.coordinates()).thenReturn(coordinates);

        var res = converter.convert(entity);

        assertNotNull(res);
        assertEquals(res.getClass(), GeoObjectPayload.class);

        assertEquals(res.getType(), "Feature");

        assertTrue(res.getProperties().containsKey("id"));
        assertEquals((Long)res.getProperties().get("id"), id);

        assertTrue(res.getProperties().containsKey("name"));
        assertEquals(res.getProperties().get("name"), name);

        assertEquals(res.getGeometry().getType(), type.toString());

        assertInstanceOf(List.class, res.getGeometry().getCoordinates().getFirst());

        List<?> items;

        items = (List<?>) res.getGeometry().getCoordinates().getFirst();
        assertEquals(0, (int)((p1lontitude - (double)items.getFirst()) * 1000000));
        assertEquals(0, (int)((p1lattitude - (double)items.getLast()) * 1000000));

        assertInstanceOf(List.class, res.getGeometry().getCoordinates().getLast());

        items = (List<?>) res.getGeometry().getCoordinates().getLast();
        assertEquals(0, (int)((p2lontitude - (double)items.getFirst()) * 1000000));
        assertEquals(0, (int)((p2lattitude - (double)items.getLast()) * 1000000));
    }

    @Test
    void testConvertFromEntityToDto_withPolygonGeometry(){
        GeoObject entity = mock(GeoObject.class);

        final var id = 3L;
        final var name = "test3";
        final var type = GeoObject.GeoObjectType.POLYGON;

        final int poly = 5;

        final var rnd = new Random();

        List<double[]> doubleCoords = new ArrayList<>();

        for(int i = 0; i < poly; i++){
            doubleCoords.add(
                    new double[]{
                        rnd.nextDouble(poly*poly),
                        rnd.nextDouble(poly*poly)
                    }
            );
        }

        final var coordinates = String.format(
                "[[%s,%s],[%s,%s],[%s,%s],[%s,%s],[%s,%s]]",
                nf.format(doubleCoords.get(0)[0]),
                nf.format(doubleCoords.get(0)[1]),
                nf.format(doubleCoords.get(1)[0]),
                nf.format(doubleCoords.get(1)[1]),
                nf.format(doubleCoords.get(2)[0]),
                nf.format(doubleCoords.get(2)[1]),
                nf.format(doubleCoords.get(3)[0]),
                nf.format(doubleCoords.get(3)[1]),
                nf.format(doubleCoords.get(4)[0]),
                nf.format(doubleCoords.get(4)[1])
        );

        when(entity.id()).thenReturn(id);
        when(entity.name()).thenReturn(name);
        when(entity.type()).thenReturn(type);
        when(entity.coordinates()).thenReturn(coordinates);

        var res = converter.convert(entity);

        assertNotNull(res);
        assertEquals(res.getClass(), GeoObjectPayload.class);

        assertEquals(res.getType(), "Feature");

        assertTrue(res.getProperties().containsKey("id"));
        assertEquals((Long)res.getProperties().get("id"), id);

        assertTrue(res.getProperties().containsKey("name"));
        assertEquals(res.getProperties().get("name"), name);

        assertEquals(res.getGeometry().getType(), type.toString());

        var list = res.getGeometry().getCoordinates().getFirst();
        List<?> items;

        for(int i = 0; i < 5; i++){
            assertInstanceOf(List.class, list);

            items = (List<?>) ((List<?>) list).get(i);
            assertEquals(0, (int)((doubleCoords.get(i)[0] - (double)items.getFirst()) * 1000000));
            assertEquals(0, (int)((doubleCoords.get(i)[1] - (double)items.getLast()) * 1000000));
        }
    }

    @Test
    void testConvertFromEntityToDto_withIncorrectNumberOfCoordinates(){
        GeoObject entity = mock(GeoObject.class);

        final var id = 4L;
        final var name = "test4";
        final var type = GeoObject.GeoObjectType.POINT;

        final var rnd = new Random();

        double lontitude = rnd.nextDouble(25);
        double lattitude = rnd.nextDouble(25);
        double extraValue = rnd.nextDouble(25);

        final var coordinates = String.format(
                "[%s,%s,%s]",
                nf.format(lontitude),
                nf.format(lattitude),
                nf.format(extraValue)
        );

        when(entity.id()).thenReturn(id);
        when(entity.name()).thenReturn(name);
        when(entity.type()).thenReturn(type);
        when(entity.coordinates()).thenReturn(coordinates);

        RuntimeException exception = Assertions.assertThrows(
                RuntimeException.class,
                () -> converter.convert(entity)
        );

        assertNotNull(exception);
        assertEquals(IllegalArgumentException.class, exception.getClass());
    }

    @Test
    void testConvertFromDtoToEntry_withPointGeometry(){
        Geometry geometry = mock(Geometry.class);
        GeoObjectPayload payload = mock(GeoObjectPayload.class);

        final var id = 1L;
        final var name = "test1";
        final var type = GeoObject.GeoObjectType.POINT;

        final var rnd = new Random();

        double lontitude = rnd.nextDouble(25);
        double lattitude = rnd.nextDouble(25);

        final var stringCoordinates = String.format(
                "[%s,%s]",
                nf.format(lontitude),
                nf.format(lattitude)
        );

        List<Object> coordinates = new ArrayList<>();
        coordinates.add(lontitude);
        coordinates.add(lattitude);

        Map<String, Object> properties = new HashMap<>();
        properties.put("id", id);
        properties.put("name", name);

        when(geometry.getType()).thenReturn(type.toString());
        when(geometry.getCoordinates()).thenReturn(coordinates);

        when(payload.getType()).thenReturn("Feature");
        when(payload.getGeometry()).thenReturn(geometry);
        when(payload.getProperties()).thenReturn(properties);

        var res = converter.convert(payload);

        assertNotNull(res);
        assertEquals(res.getClass(), GeoObject.class);

        assertEquals(id, res.id());
        assertEquals(name, res.name());
        assertEquals(GeoObject.GeoObjectType.POINT, res.type());
        assertEquals(
                stringCoordinates.replaceAll(" ", ""),
                res.coordinates().replaceAll(" ", "")
        );
    }

    @Test
    void testConvertFromDtoToEntry_withLineGeometry(){
        Geometry geometry = mock(Geometry.class);
        GeoObjectPayload payload = mock(GeoObjectPayload.class);

        final var id = 2L;
        final var name = "test2";
        final var type = GeoObject.GeoObjectType.LINE_STRING;

        final var rnd = new Random();

        double p1lontitude = rnd.nextDouble(25);
        double p1lattitude = rnd.nextDouble(25);

        double p2lontitude = rnd.nextDouble(25);
        double p2lattitude = rnd.nextDouble(25);

        final var stringCoordinates = String.format(
                "[[%s,%s],[%s,%s]]",
                nf.format(p1lontitude),
                nf.format(p1lattitude),
                nf.format(p2lontitude),
                nf.format(p2lattitude)
        );

        List<Object> coordinates = new ArrayList<>();
        coordinates.add(List.of(p1lontitude, p1lattitude));
        coordinates.add(List.of(p2lontitude, p2lattitude));

        Map<String, Object> properties = new HashMap<>();
        properties.put("id", id);
        properties.put("name", name);

        when(geometry.getType()).thenReturn(type.toString());
        when(geometry.getCoordinates()).thenReturn(coordinates);

        when(payload.getType()).thenReturn("Feature");
        when(payload.getGeometry()).thenReturn(geometry);
        when(payload.getProperties()).thenReturn(properties);

        var res = converter.convert(payload);

        assertNotNull(res);
        assertEquals(res.getClass(), GeoObject.class);

        assertEquals(id, res.id());
        assertEquals(name, res.name());
        assertEquals(GeoObject.GeoObjectType.LINE_STRING, res.type());
        assertEquals(
                stringCoordinates.replaceAll(" ", ""),
                res.coordinates().replaceAll(" ", "")
        );
    }

    @Test
    void testConvertFromDtoToEntry_withPolygonGeometry(){
        Geometry geometry = mock(Geometry.class);
        GeoObjectPayload payload = mock(GeoObjectPayload.class);

        final var id = 3L;
        final var name = "test3";
        final var type = GeoObject.GeoObjectType.POLYGON;

        final int poly = 5;

        final var rnd = new Random();

        List<double[]> doubleCoords = new ArrayList<>();

        for(int i = 0; i < poly; i++){
            doubleCoords.add(
                    new double[]{
                            rnd.nextDouble(poly*poly),
                            rnd.nextDouble(poly*poly)
                    }
            );
        }

        final var stringCoordinates = String.format(
                "[[%s, %s],[%s, %s],[%s, %s],[%s, %s],[%s, %s]]",
                nf.format(doubleCoords.get(0)[0]),
                nf.format(doubleCoords.get(0)[1]),
                nf.format(doubleCoords.get(1)[0]),
                nf.format(doubleCoords.get(1)[1]),
                nf.format(doubleCoords.get(2)[0]),
                nf.format(doubleCoords.get(2)[1]),
                nf.format(doubleCoords.get(3)[0]),
                nf.format(doubleCoords.get(3)[1]),
                nf.format(doubleCoords.get(4)[0]),
                nf.format(doubleCoords.get(4)[1])
        );

        List<Object> coordinates = new ArrayList<>();
        for(double[] pair : doubleCoords){
            coordinates.add(List.of(pair[0], pair[1]));
        }

        Map<String, Object> properties = new HashMap<>();
        properties.put("id", id);
        properties.put("name", name);

        when(geometry.getType()).thenReturn(type.toString());
        when(geometry.getCoordinates()).thenReturn(List.of(coordinates));

        when(payload.getType()).thenReturn("Feature");
        when(payload.getGeometry()).thenReturn(geometry);
        when(payload.getProperties()).thenReturn(properties);

        var res = converter.convert(payload);

        assertNotNull(res);
        assertEquals(res.getClass(), GeoObject.class);

        assertEquals(id, res.id());
        assertEquals(name, res.name());
        assertEquals(GeoObject.GeoObjectType.POLYGON, res.type());
        assertEquals(
                stringCoordinates.replaceAll(" ", ""),
                res.coordinates().replaceAll(" ", "")
        );
    }

    @Test
    void testConvertFromDtoToEntity_withIncorrectNumberOfCoordinates(){
        Geometry geometry = mock(Geometry.class);
        GeoObjectPayload payload = mock(GeoObjectPayload.class);

        final var id = 4L;
        final var name = "test4";
        final var type = GeoObject.GeoObjectType.POINT;

        final var rnd = new Random();

        double lontitude = rnd.nextDouble(25);
        double lattitude = rnd.nextDouble(25);
        double extraValue = rnd.nextDouble(25);

        List<Object> coordinates = new ArrayList<>();
        coordinates.add(lontitude);
        coordinates.add(lattitude);
        coordinates.add(extraValue);

        Map<String, Object> properties = new HashMap<>();
        properties.put("id", id);
        properties.put("name", name);

        when(geometry.getType()).thenReturn(type.toString());
        when(geometry.getCoordinates()).thenReturn(coordinates);

        when(payload.getType()).thenReturn("Feature");
        when(payload.getGeometry()).thenReturn(geometry);
        when(payload.getProperties()).thenReturn(properties);

        RuntimeException exception = Assertions.assertThrows(
                RuntimeException.class,
                () -> converter.convert(payload)
        );

        assertNotNull(exception);
        assertEquals(IllegalArgumentException.class, exception.getClass());
    }
}