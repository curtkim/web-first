import { of } from 'rxjs';
import { delay, switchMap } from 'rxjs/operators';

function simulateHttp(val, delayMillis) {
  return of(val).pipe(delay(delayMillis));
}

/*
console.log('simulating HTTP requests');

const http1$ = simulateHttp("1", 1000);

const http2$ = simulateHttp("2", 1000);

http1$.subscribe(
    console.log,
    console.error,
    () => console.log('http1$ completed')
);

http2$.subscribe(
    console.log,
    console.error,
    () => console.log('http2$ completed')
);
*/
const saveUser$ = simulateHttp(" user saved ", 1000);

const httpResult$ = saveUser$.pipe(
  switchMap(sourceValue => {
    console.log(sourceValue);
    return simulateHttp(" data reloaded ", 2000);
  })
);

httpResult$.subscribe(
    console.log,
    console.error,
    () => console.log('completed httpResult$')
);