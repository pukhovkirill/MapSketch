/* eslint-disable no-unused-vars */
import CircleStyle from 'ol/style/Circle.js';
import Fill from 'ol/style/Fill.js';
import Stroke from 'ol/style/Stroke.js';
import Style from 'ol/style/Style.js';

const imageStyle = new CircleStyle({
  radius: 6,
  fill: new Fill({color: 'red'}),
  stroke: new Stroke({color: 'white', width: '1.5'}),
});

const styles = {
  Point: new Style({image: imageStyle}),
  LineString: new Style({stroke: new Stroke({color: 'green', width: 2})}),
  Polygon: new Style({
    stroke: new Stroke({color: 'blue', lineDash: [4], width: 3}),
    fill: new Fill({color: 'rgba(0, 0, 255, 0.1)'}),
  }),
};

export const styleFunction = (feature) =>
  styles[feature.getGeometry().getType()] || null;
