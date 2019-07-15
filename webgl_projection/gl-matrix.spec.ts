import { expect } from 'chai';
import 'mocha';
import { mat4 } from "gl-matrix"

function assertArrayEqual(e, a) {
    const EPSILON = 0.00001;

    if (e.length != a.length)
        throw "length mismatch";

    for (let i = 0; i < e.length; i++) {
        if (Math.abs(e[i] - a[i]) >= EPSILON)
            throw Math.abs(e[i] - a[i]);
    }
}

describe('gl-matrix', () => {
    it('test', () => {
        const out = new Float32Array([0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0]);
        mat4.perspective(out, Math.PI * 0.5, 1, 0, 1);
        assertArrayEqual(out, [
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, -1, -1,
            0, 0, 0, 0
        ]);

        mat4.perspective(out, 45 * Math.PI / 180.0, 640/480, 0.1, 200);
        assertArrayEqual(out, [
            1.81066, 0, 0, 0,
            0, 2.414213, 0, 0,
            0, 0, -1.001, -1,
            0, 0, -0.2001, 0
        ]);

        //mat4.perspective(45, 1, 0.1, 100.0, pMatrix);

        /*
        expect(driverMap.has(1)).to.be.true;
        expect(driverMap.get(1)).to.be.not.null;
        expect(driverMap.size).to.equal(1);

        driverMap.set(2, new Driver());
        expect(driverMap.size).to.equal(2);
        */
    });
});