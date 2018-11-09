import { marbles } from "rxjs-marbles/mocha";
import { map } from "rxjs/operators";
import { throttleTime, distinctUntilChanged } from 'rxjs/operators';
import { merge, zip, from } from 'rxjs';
import {expect} from 'chai'

describe("rxjs-marbles", () => {

    it("should support marble tests", marbles(m => {

        const source =  m.hot("--^-a-b-c-|");
        const subs =            "^-------!";
        const expected =        "--b-c-d-|";

        const destination = source.pipe(
            map(value => String.fromCharCode(value.charCodeAt(0) + 1))
        );
        m.expect(destination).toBeObservable(expected);
        m.expect(source).toHaveSubscriptions(subs);
    }));

    it("merge", marbles(m => {
        var e1 = m.hot('----a--^--b-------c--|');
        var e2 = m.hot(  '---d-^--e---------f-----|');
        var expected =      '---(be)----c-f-----|';
        
        m.expect(merge(e1, e2)).toBeObservable(expected);
    }));

    it("throttleTime", marbles(m => {
        const e1 =  m.cold('-a--b--c---|');
        const subs =       '^----------!';
        const expected =   '-a-----c---|';

        m.expect(e1.pipe(throttleTime(3))).toBeObservable(expected);
        m.expect(e1).toHaveSubscriptions(subs);
    }));

    it("zip", marbles(m => {
        const e1 =  m.cold('abcde|');
        const e2 =  m.cold('12345|');
        const expected =   'ABCDE|';

        const zipped = zip(e1, e2).pipe(map(([a, b]) => `${a}${b}`) )
        m.expect(zipped).toBeObservable(expected, {A: 'a1', B: 'b2', C: 'c3', D: 'd4', E: 'e5'});
    }));

    it("distinctUntilChanged", marbles(m=> {
        const myArrayWithDuplicatesInARow = from([1, 1, 2, 2, 3, 1, 2, 3]);
        const distinct = myArrayWithDuplicatesInARow.pipe(distinctUntilChanged());
        let arr = [];
        distinct.subscribe((e)=> arr.push(e))
        expect(arr).to.eql([1,2,3,1,2,3]);
    }));

});