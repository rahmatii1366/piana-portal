import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class LocalStorageService {

  constructor() { }

  // Set a value in local storage
  setItem(key: string, value: string): void {
    console.log("setItem",  key, value);
    if (value !== null) {
      localStorage.setItem(key, value);
    } else {
      console.log("setItem value is null",  key, value);
    }
  }

  // Get a value from local storage
  getItem(key: string): string | null {
    console.log("getItem",  key, localStorage.getItem(key));
    if (localStorage.getItem(key) === 'null')
      this.removeItem(key);
    return localStorage.getItem(key);
  }

  // Remove a value from local storage
  removeItem(key: string): void {
    localStorage.removeItem(key);
  }

  // Clear all items from local storage
  clear(): void {
    localStorage.clear();
  }
}
