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
package io.nuls.token.base;

import io.nuls.contract.sdk.Address;
import io.nuls.contract.sdk.annotation.View;
import io.nuls.token.interfaces.INRC721Enumerable;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static io.nuls.contract.sdk.Utils.require;

/**
 * @author: PierreLuo
 * @date: 2019-06-05
 */
public class NRC721EnumerableBase extends NRC721Base implements INRC721Enumerable {

    private Map<Address, LinkedList<BigInteger>> ownedTokens = new HashMap<Address, LinkedList<BigInteger>>();
    private Map<BigInteger, Integer> ownedTokensIndex = new HashMap<BigInteger, Integer>();
    private LinkedList<BigInteger> allTokens = new LinkedList<BigInteger>();
    private Map<BigInteger, Integer> allTokensIndex = new HashMap<BigInteger, Integer>();

    public NRC721EnumerableBase() {
        super.registerInterface("INRC721Enumerable");
    }

    @Override
    @View
    public int totalSupply() {
        return allTokens.size();
    }

    @Override
    @View
    public BigInteger tokenOfOwnerByIndex(Address owner, int index) {
        require(index < balanceOf(owner), "NRC721Enumerable: owner index out of bounds");
        LinkedList<BigInteger> tokens = ownedTokens.get(owner);
        if(tokens == null) {
            return null;
        }
        return tokens.get(index);
    }

    @Override
    @View
    public BigInteger tokenByIndex(int index) {
        require(index < totalSupply(), "NRC721Enumerable: global index out of bounds");
        return allTokens.get(index);
    }

    @Override
    public void transferFrom(Address from, Address to, BigInteger tokenId) {
        super.transferFrom(from, to, tokenId);

        removeTokenFromOwnerEnumeration(from, tokenId);

        addTokenToOwnerEnumeration(to, tokenId);
    }

    @Override
    public void safeTransferFrom(Address from, Address to, BigInteger tokenId) {
        this.safeTransferFrom(from, to, tokenId, "");
    }

    @Override
    public void safeTransferFrom(Address from, Address to, BigInteger tokenId, String data) {
        this.transferFrom(from, to, tokenId);
        // checkOnERC721Received 的作用是当to是合约地址时，那么to这个合约必须实现`onERC721Received`函数 / data 的作用是附加备注
        require(super.checkOnNRC721Received(from, to, tokenId, data), "NRC721: transfer to non ERC721Receiver implementer");
    }

    @Override
    protected void mintBase(Address to, BigInteger tokenId) {
        super.mintBase(to, tokenId);

        addTokenToOwnerEnumeration(to, tokenId);

        addTokenToAllTokensEnumeration(tokenId);
    }

    @Override
    protected void burnBase(Address owner, BigInteger tokenId) {
        super.burnBase(owner, tokenId);

        removeTokenFromOwnerEnumeration(owner, tokenId);
        // Since tokenId will be deleted, we can clear its slot in _ownedTokensIndex to trigger a gas refund
        ownedTokensIndex.remove(tokenId);

        removeTokenFromAllTokensEnumeration(tokenId);
    }

    protected LinkedList<BigInteger> tokensOfOwner(Address owner) {
        return ownedTokens.get(owner);
    }

    private void addTokenToOwnerEnumeration(Address to, BigInteger tokenId) {
        int index = 0;
        LinkedList<BigInteger> tokens = ownedTokens.get(to);
        if(tokens == null) {
            tokens = new LinkedList<BigInteger>();
        }
        ownedTokensIndex.put(tokenId, tokens.size());
        tokens.add(tokenId);
    }

    private void addTokenToAllTokensEnumeration(BigInteger tokenId) {
        allTokensIndex.put(tokenId, allTokens.size());
        allTokens.add(tokenId);
    }

    private void removeTokenFromOwnerEnumeration(Address from, BigInteger tokenId) {
        // To prevent a gap in from's tokens array, we store the last token in the index of the token to delete, and
        // then delete the last slot (swap and pop).

        LinkedList<BigInteger> tokens = ownedTokens.get(from);
        int lastTokenIndex = tokens.size() - 1;
        int tokenIndex = ownedTokensIndex.get(tokenId);

        BigInteger lastTokenId = tokens.removeLast();
        // When the token to delete is the last token, the swap operation is unnecessary
        if (tokenIndex != lastTokenIndex) {
            tokens.remove(tokenIndex);
            tokens.add(tokenIndex, lastTokenId);
            ownedTokensIndex.put(lastTokenId, tokenIndex); // Update the moved token's index
        }

    }

    private void removeTokenFromAllTokensEnumeration(BigInteger tokenId) {
        // To prevent a gap in the tokens array, we store the last token in the index of the token to delete, and
        // then delete the last slot (swap and pop).

        int tokenIndex = allTokensIndex.get(tokenId);

        // When the token to delete is the last token, the swap operation is unnecessary. However, since this occurs so
        // rarely (when the last minted token is burnt) that we still do the swap here to avoid the gas cost of adding
        // an 'if' statement (like in _removeTokenFromOwnerEnumeration)
        BigInteger lastTokenId = allTokens.removeLast();

        allTokens.remove(tokenIndex);
        allTokens.add(tokenIndex, lastTokenId);
        ownedTokensIndex.put(lastTokenId, tokenIndex); // Update the moved token's index

        // This also deletes the contents at the last position of the array
        allTokensIndex.remove(tokenId);
    }

}
