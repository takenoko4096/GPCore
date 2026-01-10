package com.gmail.subnokoii78.gpcore.vector;

import com.gmail.subnokoii78.gpcore.generic.TriFunction;
import org.jspecify.annotations.NullMarked;

import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

/**
 * ベクトルを表現するinterface
 * @param <T> このinterfaceの実装クラス
 * @param <U> 扱う数値型
 */
@NullMarked
public interface VectorBuilder<T extends VectorBuilder<T, U>, U extends Number> {
    /**
     * 二つのベクトルが等しいかどうかを確かめます。
     * @param other もう一方のベクトル
     * @return 一致していれば真
     */
    boolean equals(T other);

    /**
     * このベクトルのそれぞれの成分に対して関数を呼び出し、その結果で成分の値を上書きします。
     * @param operator 関数
     * @return このベクトル
     */
    @Destructive
    T calculate(UnaryOperator<U> operator);

    /**
     * 引数に渡されたベクトルとこのベクトルのそれぞれの成分に対して関数を呼び出し、その結果で成分の値を上書きします。
     * @param operator 関数
     * @return このベクトル
     */
    @Destructive
    T calculate(T other, BiFunction<U, U, U> operator);

    /**
     * 引数に渡された2つのベクトルとこのベクトルのそれぞれの成分に対して関数を呼び出し、その結果で成分の値を上書きします。
     */
    @Destructive
    T calculate(T other1, T other2, TriFunction<U, U, U, U> operator);

    /**
     * 引数に渡された値との足し算を行って自身を返します。
     * @param other 他のインスタンス
     * @return このインスタンス自身
     */
    @Destructive
    T add(T other);

    /**
     * 引数に渡された値との引き算を行って自身を返します。
     * @param other 他のインスタンス
     * @return このインスタンス自身
     */
    @Destructive
    T subtract(T other);

    /**
     * このベクトルの各成分に引数に渡された値を掛けて自身を返します。
     * @param scalar 実数
     * @return このインスタンス自身
     */
    @Destructive
    T scale(U scalar);

    /**
     * ベクトルの向きを逆向きにして自身を返します。
     * @return このインスタンス自身
     */
    @Destructive
    T invert();

    /**
     * ベクトルの各成分の値の範囲を制限します。
     * @return this
     */
    @Destructive
    T clamp(T min, T max);

    /**
     * このベクトルを文字列化します。
     * @return 文字列化されたベクトル
     */
    @Override
    String toString();

    /**
     * このベクトルのコピーを返します。
     * @return コピーされたベクトル
     */
    T copy();

    /**
     * このベクトルの全成分が0であれば真を返します。
     * @return components.every(v => v === 0);
     */
    boolean isZero();
}
