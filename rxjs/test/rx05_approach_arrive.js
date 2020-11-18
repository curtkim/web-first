import { interval, BehaviorSubject, concat } from 'rxjs';
import { map, distinctUntilChanged, takeWhile, take } from 'rxjs/operators';


const config$ = new BehaviorSubject(1000)


const distance$ = concat(
  interval(50).pipe(
    map(it=> 100-it),
    takeWhile(it => it > 0)
  ),
  interval(50).pipe(
    takeWhile(it => it < 100)
  )
)

function mystring(dist) {
  if( dist > 50)
    return "far"
  else if ( dist > 10)
    return "approach"
  else
    return "arrive"
}

const result$ = distance$.pipe(
  map(mystring),
  distinctUntilChanged(),
  take(3),
)

distance$.subscribe(console.log)
result$.subscribe(console.log)

/*
setTimeout(function(){
  config$.next(100)
}, 3000)

setTimeout(function(){
  config$.next(200)
}, 5000)
*/