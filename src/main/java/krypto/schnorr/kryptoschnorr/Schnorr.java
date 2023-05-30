package krypto.schnorr.kryptoschnorr;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class Schnorr {
    private final int numberOfBitsNumberP = 512;
    private final int numberOfBitsNumberQ = 140;

    BigInteger P;
    BigInteger Q;
    BigInteger A;
    BigInteger H;
    BigInteger V;

    public Schnorr() {
        generateQ();
        generateP();
        generateH();
        generateA();
        generateV();
    }

    private void generateQ() {
        this.Q = BigInteger.probablePrime(numberOfBitsNumberQ, new Random());
    }

    private void generateP() {
        do {
            BigInteger r = BigInteger.probablePrime(this.numberOfBitsNumberP - this.numberOfBitsNumberQ, new Random());
            this.P = r.subtract(BigInteger.ONE);
            this.P = r.subtract(this.P.remainder(this.Q));
        }while(!this.P.isProbablePrime(2));
    }

    private void generateA() {
        BigInteger A;
        do {
            A = new BigInteger(this.numberOfBitsNumberQ - 2, new Random());
        } while (A.compareTo(BigInteger.ONE) != 1);
        this.A = A;
    }

    private void generateH() {

        BigInteger x = BigInteger.TWO;
        this.H = x.modPow(this.P.subtract(BigInteger.ONE).divide(this.Q),this.P);
        while(this.H.equals(BigInteger.ONE))
        {
            x = x.add(BigInteger.ONE);
            this.H = x.modPow(this.P.subtract(BigInteger.ONE).divide(this.Q),this.P);
        }    }

    private void generateV() {
        this.V = this.H.modPow(this.A, this.P);
        this.V = this.V.modInverse(this.P);
    }


    public BigInteger[] generateSignature(byte[] M) {
        BigInteger r = new BigInteger(this.numberOfBitsNumberQ - 2, new Random());

        BigInteger X = this.H.modPow(r, this.P);
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] Xbytes = X.toByteArray();
            byte[] mx = new byte[M.length + Xbytes.length];
            System.arraycopy(M, 0, mx, 0, M.length);
            System.arraycopy(Xbytes, 0, mx, M.length, Xbytes.length);
            sha256.update(mx);
            BigInteger s1 = new BigInteger(1, sha256.digest());
            BigInteger s2 = (r.add((this.A.multiply(s1))).mod(this.Q));
            BigInteger[] sign = new BigInteger[2];
            sign[0] = s1;
            sign[1] = s2;
            return sign;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean verifySignature(byte[] M, BigInteger[] sign) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            BigInteger Z = this.H.modPow(sign[1], this.P).multiply(this.V.modPow(sign[0], this.P)).mod(this.P);
            byte[] Zbytes = Z.toByteArray();
            byte[] mz = new byte[M.length + Zbytes.length];
            System.arraycopy(M, 0, mz, 0, M.length);
            System.arraycopy(Zbytes, 0, mz, M.length, Zbytes.length);
            sha256.update(mz);
            BigInteger s1 = new BigInteger(1, sha256.digest());
            return (s1.compareTo(sign[0]) == 0);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}



