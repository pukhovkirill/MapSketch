/* eslint-disable no-console */
import {map, source} from '../map/map.js';
import {renameFeatureInServer} from '../api/geoObjectsService.js';
import {selectInteraction} from '../interactions/modify.js';

export const objectListEl = document.getElementById('object-list');
const sidebarItems = {};
const sidebarNames = new Map();

export function sortSidebarList() {
  // сollect all <li> elements into an array
  const items = Array.from(objectListEl.children);
  items.sort((a, b) => {
    const keyA = a.dataset.key;
    const keyB = b.dataset.key;
    // if both keys are numbers, compare them as numbers
    const numA = parseFloat(keyA);
    const numB = parseFloat(keyB);
    if (!isNaN(numA) && !isNaN(numB)) {
      return numA - numB;
    }
    return keyA.localeCompare(keyB, undefined, {numeric: true});
  });
  // Reorder the DOM
  items.forEach((li) => objectListEl.appendChild(li));
}

export function changeIdOnSidebar(oldKey, newKey) {
  const li = sidebarItems[oldKey];
  if (!li) {
    return;
  }

  li.dataset.key = newKey;
  sidebarItems[newKey] = li;
  delete sidebarItems[oldKey];

  const name = li.textContent;
  if (sidebarNames.get(name) === oldKey) {
    sidebarNames.set(name, newKey);
  }

  sortSidebarList();
}

export function addToSidebarList(key, name) {
  if (sidebarNames.has(name)) {
    alert("Object with name '" + name + "' already exists");
    return false;
  }
  const li = document.createElement('li');
  li.textContent = name;
  li.dataset.key = key; // stores either a tempId or the real id

  li.addEventListener('click', (evt) => {
    const actualKey = evt.currentTarget.dataset.key;
    onSidebarItemClick(actualKey);
  });

  li.addEventListener('dblclick', async (evt) => {
    const actualKey = evt.currentTarget.dataset.key;
    // Search for the feature by server id
    let targetFeature = source
      .getFeatures()
      .find((f) => String(f.get('id')) === String(actualKey));
    // If not found, search by tempId
    if (!targetFeature) {
      targetFeature = source
        .getFeatures()
        .find((f) => String(f.get('tempId')) === String(actualKey));
    }
    if (!targetFeature) {
      return false;
    }

    const oldName = targetFeature.get('name') || '';
    const newName = prompt('Enter a new name for the feature:', oldName);
    if (newName === null) {
      return; // cancel
    }
    if (newName.trim() === '') {
      alert('Name cannot be empty');
      return false;
    }

    // Update the name on the feature and in the list
    targetFeature.set('name', newName);
    li.textContent = newName;

    // If there's a server id, push to the server
    const serverId = targetFeature.get('id');
    if (serverId) {
      try {
        await renameFeatureInServer(targetFeature);
      } catch (err) {
        console.error('Failed to rename feature:', err);
        alert('Error saving the new name to the server.');
      }
    }
    // Otherwise, on bulk-save the new name will still be sent
  });

  objectListEl.appendChild(li);
  sidebarNames.set(name, key);
  sidebarItems[key] = li;

  sortSidebarList();

  return true;
}

export function removeFromSidebarList(key) {
  const li = sidebarItems[key];
  if (li) {
    li.remove();
    delete sidebarItems[key];
    sidebarNames.delete(li.textContent);
  }
}

export function onSidebarItemClick(key) {
  let feature = source
    .getFeatures()
    .find((f) => String(f.get('id')) === String(key));
  if (!feature) {
    feature = source
      .getFeatures()
      .find((f) => String(f.get('tempId')) === String(key));
    if (!feature) {
      return;
    }
  }

  const geometry = feature.getGeometry();
  let center;
  if (geometry.getType() === 'Point') {
    center = geometry.getCoordinates();
  } else if (geometry.getType() === 'LineString') {
    center = geometry.getFirstCoordinate();
  } else {
    center = geometry.getInteriorPoint().getCoordinates();
  }

  map.getView().animate({center, duration: 500});
  selectInteraction.getFeatures().clear();
  selectInteraction.getFeatures().push(feature);
}
