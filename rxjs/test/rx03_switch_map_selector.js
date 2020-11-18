import { of, interval } from 'rxjs';
import { delay, map, switchMap } from 'rxjs/operators';

function simulateHttp(val, delayMillis) {
  return of(val).pipe(delay(delayMillis));
}

const course$ = simulateHttp({id:1, description: 'Angular For Beginners'}, 100);

const httpResult$ = course$.pipe(
  switchMap(
    courses => simulateHttp([1,2,3], 200),
    (courses, lessons, outerIndex, innerIndex) => [courses, lessons, outerIndex, innerIndex] 
  )
);

httpResult$.subscribe(
    console.log,
    console.error,
    () => console.log('completed httpResult$')
);