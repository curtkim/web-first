import { of, Subject, BehaviorSubject, range, concat } from 'rxjs';
import { buffer, delay, finalize, map, flatMap, concatMap, filter, tap, distinct } from 'rxjs/operators';

/*
let dist$ = range(0, 10).pipe(
  map(val => 10 - val)
)
*/
let dist$ = concat(
  range(0, 10).pipe(
    map(val => 10 - val)
  ),
  range(1, 10)
)

dist$.pipe(
  map(v => {
    if(v <= 2)
      return "Arrive"
    else if(v <= 4)
      return "Approach"
    else
      return "Far"    
  }),
  distinct(),
).subscribe(console.log)

/*
concat(
  range(1, 10),
  range(0, 10).pipe(
    map(val => 10 - val)
  )
).subscribe(console.log)
*/