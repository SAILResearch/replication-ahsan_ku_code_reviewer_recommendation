package com.sail.java.exam.model.weight;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.csvreader.CsvWriter;

import org.apache.commons.lang3.ArrayUtils;

public class WeightCombinationCreator {

    double weightValues[] = new double[] {
        0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9
    };

    public List<List<Double>> combine(int n, int k) {
        if (k == n || k == 0) {
            List<Double> row = new LinkedList<>();
            for (int i = 0; i < k; ++i) {
                row.add(this.weightValues[i]);
            }
            return new LinkedList<>(Arrays.asList(row));
        }
        List<List<Double>> result = this.combine(n - 1, k - 1);
        result.forEach(e -> e.add(this.weightValues[n]));
        result.addAll(this.combine(n - 1, k));
        return result;
    }

    public List<List<Integer>> combine2(int n, int k) {
        if (k == n || k == 0) {
            List<Integer> row = new LinkedList<>();
            for (int i = 1; i <= k; ++i) {
                row.add(i);
            }
            return new LinkedList<>(Arrays.asList(row));
        }
        List<List<Integer>> result = this.combine2(n, k - 1);
        result.forEach(e -> e.add(n));
        result.addAll(this.combine2(n - 1, k));
        return result;
    }


    double getSum(double... v) {
        double result = 0;
        for (int i = 0; i < v.length; i++) {
            result = result + v[i];
        }
        return result;
    }

    public void allPossibleCaseGenerator(double start, double end, double step) throws Exception {
        CsvWriter writer = new CsvWriter("/home/local/SAIL/ahsan/BACKUP/ahsan_project_2021/ahsan_gustavo/dev_knowledge/weight_analysis_ser/weight_comb.csv");
        for(int i = 0 ; i < 29 ; i ++){
            writer.write(String.format("%d", i));
        }
        writer.endRecord();
        long total = 0;
        for (double a = start; a < end; a += step) {
            for (double b = start; b < end; b += step) {
                if (getSum(a, b) > 1)
                    continue;
                for (double c = start; c < end; c += step) {
                    if (getSum(a, b, c) > 1)
                        continue;
                    for (double d = start; d < end; d += step) {
                        if (getSum(a, b, c, d) > 1)
                            continue;
                        for (double e = start; e < end; e += step) {
                            if (getSum(a, b, c, d, e) > 1)
                                continue;
                            for (double f = start; f < end; f += step) {
                                if (getSum(a, b, c, d, e, f) > 1)
                                    continue;
                                for (double g = start; g < end; g += step) {
                                    if (getSum(a, b, c, d, e, f, g) > 1)
                                        continue;
                                    for (double h = start; h < end; h += step) {
                                        if (getSum(a, b, c, d, e, f, g, h) > 1)
                                            continue;
                                        for (double i = start; i < end; i += step) {
                                            if (getSum(a, b, c, d, e, f, g, h, i) > 1)
                                                continue;
                                            for (double j = start; j < end; j += step) {
                                                if (getSum(a, b, c, d, e, f, g, h, i, j) > 1)
                                                    continue;
                                                for (double k = start; k < end; k += step) {
                                                    if (getSum(a, b, c, d, e, f, g, h, i, j, k) > 1)
                                                        continue;
                                                    for (double l = start; l < end; l += step) {
                                                        if (getSum(a, b, c, d, e, f, g, h, i, j, k, l) > 1)
                                                            continue;
                                                        for (double m = start; m < end; m += step) {
                                                            if (getSum(a, b, c, d, e, f, g, h, i, j, k, l, m) > 1)
                                                                continue;
                                                            for (double n = start; n < end; n += step) {
                                                                if (getSum(a, b, c, d, e, f, g, h, i, j, k, l, m,
                                                                        n) > 1)
                                                                    continue;
                                                                for (double o = start; o < end; o += step) {
                                                                    if (getSum(a, b, c, d, e, f, g, h, i, j, k, l, m, n,
                                                                            o) > 1)
                                                                        continue;
                                                                    for (double p = start; p < end; p += step) {
                                                                        if (getSum(a, b, c, d, e, f, g, h, i, j, k, l,
                                                                                m, n, o, p) > 1)
                                                                            continue;
                                                                        for (double q = start; q < end; q += step) {
                                                                            if (getSum(a, b, c, d, e, f, g, h, i, j, k,
                                                                                    l, m, n, o, p, q) > 1)
                                                                                continue;
                                                                            for (double r = start; r < end; r += step) {
                                                                                if (getSum(a, b, c, d, e, f, g, h, i, j,
                                                                                        k, l, m, n, o, p, q, r) > 1)
                                                                                    continue;
                                                                                for (double s = start; s < end; s += step) {
                                                                                    if (getSum(a, b, c, d, e, f, g, h,
                                                                                            i, j, k, l, m, n, o, p, q,
                                                                                            r, s) > 1)
                                                                                        continue;
                                                                                    for (double t = start; t < end; t += step) {
                                                                                        if (getSum(a, b, c, d, e, f, g,
                                                                                                h, i, j, k, l, m, n, o,
                                                                                                p, q, r, s, t) > 1)
                                                                                            continue;
                                                                                        for (double u = start; u < end; u += step) {
                                                                                            if (getSum(a, b, c, d, e, f,
                                                                                                    g, h, i, j, k, l, m,
                                                                                                    n, o, p, q, r, s, t,
                                                                                                    u) > 1)
                                                                                                continue;
                                                                                            for (double v = start; v < end; v += step) {
                                                                                                if (getSum(a, b, c, d,
                                                                                                        e, f, g, h, i,
                                                                                                        j, k, l, m, n,
                                                                                                        o, p, q, r, s,
                                                                                                        t, u, v) > 1)
                                                                                                    continue;
                                                                                                for (double w = start; w < end; w += step) {
                                                                                                    if (getSum(a, b, c,
                                                                                                            d, e, f, g,
                                                                                                            h, i, j, k,
                                                                                                            l, m, n, o,
                                                                                                            p, q, r, s,
                                                                                                            t, u, v,
                                                                                                            w) > 1)
                                                                                                        continue;
                                                                                                    for (double x = start; x < end; x += step) {
                                                                                                        if (getSum(a, b,
                                                                                                                c, d, e,
                                                                                                                f, g, h,
                                                                                                                i, j, k,
                                                                                                                l, m, n,
                                                                                                                o, p, q,
                                                                                                                r, s, t,
                                                                                                                u, v, w,
                                                                                                                x) > 1)
                                                                                                            continue;
                                                                                                        for (double y = start; y < end; y += step) {
                                                                                                            if (getSum(
                                                                                                                    a,
                                                                                                                    b,
                                                                                                                    c,
                                                                                                                    d,
                                                                                                                    e,
                                                                                                                    f,
                                                                                                                    g,
                                                                                                                    h,
                                                                                                                    i,
                                                                                                                    j,
                                                                                                                    k,
                                                                                                                    l,
                                                                                                                    m,
                                                                                                                    n,
                                                                                                                    o,
                                                                                                                    p,
                                                                                                                    q,
                                                                                                                    r,
                                                                                                                    s,
                                                                                                                    t,
                                                                                                                    u,
                                                                                                                    v,
                                                                                                                    w,
                                                                                                                    x,
                                                                                                                    y) > 1)
                                                                                                                continue;
                                                                                                            for (double z = start; z < end; z += step) {
                                                                                                                if (getSum(
                                                                                                                        a,
                                                                                                                        b,
                                                                                                                        c,
                                                                                                                        d,
                                                                                                                        e,
                                                                                                                        f,
                                                                                                                        g,
                                                                                                                        h,
                                                                                                                        i,
                                                                                                                        j,
                                                                                                                        k,
                                                                                                                        l,
                                                                                                                        m,
                                                                                                                        n,
                                                                                                                        o,
                                                                                                                        p,
                                                                                                                        q,
                                                                                                                        r,
                                                                                                                        s,
                                                                                                                        t,
                                                                                                                        u,
                                                                                                                        v,
                                                                                                                        w,
                                                                                                                        x,
                                                                                                                        y,
                                                                                                                        z) > 1)
                                                                                                                    continue;
                                                                                                                for (double aa = start; aa < end; aa += step) {
                                                                                                                    if (getSum(
                                                                                                                            a,
                                                                                                                            b,
                                                                                                                            c,
                                                                                                                            d,
                                                                                                                            e,
                                                                                                                            f,
                                                                                                                            g,
                                                                                                                            h,
                                                                                                                            i,
                                                                                                                            j,
                                                                                                                            k,
                                                                                                                            l,
                                                                                                                            m,
                                                                                                                            n,
                                                                                                                            o,
                                                                                                                            p,
                                                                                                                            q,
                                                                                                                            r,
                                                                                                                            s,
                                                                                                                            t,
                                                                                                                            u,
                                                                                                                            v,
                                                                                                                            w,
                                                                                                                            x,
                                                                                                                            y,
                                                                                                                            z,
                                                                                                                            aa) > 1)
                                                                                                                        continue;
                                                                                                                    for (double ab = start; ab < end; ab += step) {
                                                                                                                        if (getSum(
                                                                                                                                a,
                                                                                                                                b,
                                                                                                                                c,
                                                                                                                                d,
                                                                                                                                e,
                                                                                                                                f,
                                                                                                                                g,
                                                                                                                                h,
                                                                                                                                i,
                                                                                                                                j,
                                                                                                                                k,
                                                                                                                                l,
                                                                                                                                m,
                                                                                                                                n,
                                                                                                                                o,
                                                                                                                                p,
                                                                                                                                q,
                                                                                                                                r,
                                                                                                                                s,
                                                                                                                                t,
                                                                                                                                u,
                                                                                                                                v,
                                                                                                                                w,
                                                                                                                                x,
                                                                                                                                y,
                                                                                                                                z,
                                                                                                                                aa,
                                                                                                                                ab) > 1)
                                                                                                                            continue;
                                                                                                                        for (double ac = start; ac < end; ac += step) {
                                                                                                                            if (getSum(
                                                                                                                                    a,
                                                                                                                                    b,
                                                                                                                                    c,
                                                                                                                                    d,
                                                                                                                                    e,
                                                                                                                                    f,
                                                                                                                                    g,
                                                                                                                                    h,
                                                                                                                                    i,
                                                                                                                                    j,
                                                                                                                                    k,
                                                                                                                                    l,
                                                                                                                                    m,
                                                                                                                                    n,
                                                                                                                                    o,
                                                                                                                                    p,
                                                                                                                                    q,
                                                                                                                                    r,
                                                                                                                                    s,
                                                                                                                                    t,
                                                                                                                                    u,
                                                                                                                                    v,
                                                                                                                                    w,
                                                                                                                                    x,
                                                                                                                                    y,
                                                                                                                                    z,
                                                                                                                                    aa,
                                                                                                                                    ab,
                                                                                                                                    ac) > 1)
                                                                                                                                continue;
                                                                                                                            double[] arrayValues = new double[] {
                                                                                                                                    a,
                                                                                                                                    b,
                                                                                                                                    c,
                                                                                                                                    d,
                                                                                                                                    e,
                                                                                                                                    f,
                                                                                                                                    g,
                                                                                                                                    h,
                                                                                                                                    i,
                                                                                                                                    j,
                                                                                                                                    k,
                                                                                                                                    l,
                                                                                                                                    m,
                                                                                                                                    n,
                                                                                                                                    o,
                                                                                                                                    p,
                                                                                                                                    q,
                                                                                                                                    r,
                                                                                                                                    s,
                                                                                                                                    t,
                                                                                                                                    u,
                                                                                                                                    v,
                                                                                                                                    w,
                                                                                                                                    x,
                                                                                                                                    y,
                                                                                                                                    z,
                                                                                                                                    aa,
                                                                                                                                    ab,
                                                                                                                                    ac };
                                                                                                                           
                                                                                                                                    for(int ii = 0 ; ii < arrayValues.length ; ii++){
                                                                                                                                        writer.write(String.format("%.3f", arrayValues[ii]));
                                                                                                                                    }
                                                                                                                                    writer.endRecord();
                                                                                                                                    total ++;

                                                                                                                                    if(total % 100000 == 0){
                                                                                                                                        System.out.println("Finish: " + total);
                                                                                                                                    }
                                                                                                                        }
                                                                                                                    }
                                                                                                                }
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        writer.close();
    }
}
