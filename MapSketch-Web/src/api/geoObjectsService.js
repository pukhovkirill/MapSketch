/* eslint-disable no-console */
import GeoJSON from 'ol/format/GeoJSON.js';
import {addToSidebarList, changeIdOnSidebar} from '../ui/sidebar.js';
import {map, source} from '../map/map.js';

const format = new GeoJSON();
let apiUrl = 'http://localhost:8080/api/v1/geoobjects';

export function setServerDomain(domain) {
  if (domain !== null || domain !== undefined) {
    apiUrl = domain + '/api/v1/geoobjects';
  }
}

export async function renameFeatureInServer(feature) {
  if (feature.get('id')) {
    // Update existing feature
    const id = feature.get('id');
    const response = await fetch(`${apiUrl}/${id}`, {
      method: 'PUT',
      headers: {'Content-Type': 'application/text'},
      body: feature.get('name'),
    });
    if (!response.ok) {
      throw new Error(`Error updating feature (status ${response.status})`);
    }
  }
}

export async function saveFeaturesToServer(features) {
  // Bulk-save multiple features
  const geojsonFormat = new GeoJSON({featureProjection: 'EPSG:3857'});
  const geojsonStr = geojsonFormat.writeFeatures(features);

  const response = await fetch(apiUrl, {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: geojsonStr,
  });

  if (!response.ok) {
    throw new Error(`Error during bulk save (status ${response.status})`);
  }

  const data = await response.json();
  const items = new Map();

  data.features.forEach((obj) => {
    items.set(obj.properties.name, obj.properties.id);
  });

  for (let i = 0; i < features.length; i++) {
    const feature = features[i];
    const id = items.get(feature.get('name'));
    feature.set('id', id);
    feature.unset('isNewFeature');
    feature.unset('isModified');
    changeIdOnSidebar(feature.get('tempId'), id);
  }
}

export async function deleteFeatureFromServer(feature) {
  const id = feature.get('id');
  if (!id) {
    return;
  }
  const response = await fetch(`${apiUrl}/${id}`, {
    method: 'DELETE',
  });
  if (!response.ok) {
    throw new Error(`Error deleting feature (status ${response.status})`);
  }
}

export async function loadFeaturesFromServer() {
  try {
    const response = await fetch(`${apiUrl}/list`, {
      method: 'GET',
    });
    if (!response.ok) {
      throw new Error(`Error loading features (status ${response.status})`);
    }
    const data = await response.json();
    const features = data.features;
    features.forEach((item) => {
      const feature = format.readFeature(
        {
          type: 'Feature',
          geometry: item.geometry,
          properties: {name: item.properties.name, id: item.properties.name},
        },
        {featureProjection: map.getView().getProjection()},
      );
      feature.set('id', item.properties.id);
      feature.set('name', item.properties.name);
      source.addFeature(feature);
      addToSidebarList(item.properties.id, item.properties.name);
    });
  } catch (err) {
    console.error('Failed to load features from server:', err);
    alert('Failed to load features from the server.');
  }
}
