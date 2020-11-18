import { of, interval } from 'rxjs';
import { delay, map, switchMap } from 'rxjs/operators';

function simulateFirebase(val, delayMilli) {
  return interval(delayMilli).pipe(
    map(index => val + " " + index)
  );
}

/*
const firebase1$ = simulateFirebase("FB-1 ", 5000);
const firebase2$ = simulateFirebase("FB-2 ", 1000);

firebase1$.subscribe(
    console.log,
    console.error,
    () => console.log('firebase1$ completed')
);

firebase2$.subscribe(
    console.log,
    console.error,
    () => console.log('firebase2$ completed')
);
*/
const firebase1$ = simulateFirebase("FB-1 ", 5000);

const firebaseResult$ = firebase1$.pipe(
  switchMap(sourceValue => {
    console.log("source value " + sourceValue);
    return simulateFirebase("inner observable ", 1000)
  })
);

firebaseResult$.subscribe(
  console.log,
  console.error,
  () => console.log('completed firebaseResult$')
);