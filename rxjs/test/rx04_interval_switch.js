import { interval, BehaviorSubject } from 'rxjs';
import { switchMap, sample } from 'rxjs/operators';


const config$ = new BehaviorSubject(1000)

const gpsOrigin$ = interval(100)


const gps$ = config$.pipe(
  switchMap(second => {
    console.log("===", second)
    return gpsOrigin$.pipe(
      sample(interval(second))
    )
  })
)

gps$.subscribe(console.log)

setTimeout(function(){
  config$.next(100)
}, 3000)

setTimeout(function(){
  config$.next(200)
}, 5000)