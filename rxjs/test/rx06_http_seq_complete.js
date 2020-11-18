// seq http

import { of, Subject, BehaviorSubject } from 'rxjs';
import { buffer, delay, finalize, map, flatMap, concatMap, filter, tap } from 'rxjs/operators';

const req$ = new Subject()

function simulateHttp(val, delayMillis) {
  return of(val).pipe(
    delay(delayMillis),
  );
}

req$.pipe(  
  concatMap(it => simulateHttp(it[0], it[1]))
).subscribe(console.log)

console.log("=== 0")
req$.next([1, 100])
console.log("=== 1")
req$.next([2, 1000])
req$.next([3, 1000])
req$.next([4, 1000])


setTimeout(function(){
  console.log('done')
}, 5000)

