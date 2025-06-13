/* eslint-disable import/named */
import Map from 'ol/Map.js';
import OSM from 'ol/source/OSM.js';
import TileLayer from 'ol/layer/Tile.js';
import VectorLayer from 'ol/layer/Vector.js';
import VectorSource from 'ol/source/Vector.js';
import View from 'ol/View.js';
import {fromLonLat} from 'ol/proj.js';
import {styleFunction} from '../styles/featureStyles.js';

export const source = new VectorSource();

const vectorLayer = new VectorLayer({
  source,
  style: styleFunction,
});

export const map = new Map({
  target: 'map',
  layers: [new TileLayer({source: new OSM()}), vectorLayer],
  view: new View({
    center: fromLonLat([37.6173, 55.7558]),
    zoom: 10,
  }),
});
