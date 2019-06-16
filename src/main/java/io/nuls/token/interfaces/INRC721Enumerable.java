/**
 * MIT License
 * <p>
 * Copyright (c) 2017-2018 nuls.io
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.nuls.token.interfaces;

import io.nuls.contract.sdk.Address;
import io.nuls.contract.sdk.annotation.View;

import java.math.BigInteger;

/**
 * @author: PierreLuo
 * @date: 2019-06-04
 */
public interface INRC721Enumerable {
    /**
     * Count NFTs tracked by this contract
     * @return A count of valid NFTs tracked by this contract, where each one of
     *         them has an assigned and queryable owner
     */
    @View
    int totalSupply();

    /**
     * Enumerate valid NFTs
     * @param index A counter less than `totalSupply()`
     * @return The token identifier for the `index` NFT,
     *  (sort order not specified), NULL if `index` >= `totalSupply()`.
     */
    @View
    BigInteger tokenByIndex(int index);

    /**
     * Enumerate NFTs assigned to an owner
     * @param owner An address where we are interested in NFTs owned by them
     * @param index A counter less than `balanceOf(_owner)`
     * @return The token identifier for the `index` NFT assigned to `owner`,
     *   (sort order not specified), NULL if `index` >= `balanceOf(owner)` or invalid NFTs.
     */
    @View
    BigInteger tokenOfOwnerByIndex(Address owner, int index);
}
