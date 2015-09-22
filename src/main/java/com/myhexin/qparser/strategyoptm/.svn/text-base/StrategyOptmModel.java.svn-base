/**
 * 
 */
package com.myhexin.qparser.strategyoptm;

import java.util.HashSet;
import java.util.Set;

/**
 * @author chenhao
 *
 */
public class StrategyOptmModel {
	private Set<String> dates = new HashSet<String>();
	private Set<String> indexs = new HashSet<String>();
	private Set<String> stkCodes = new HashSet<String>();
	private Set<String> semantcis = new HashSet<String>();

	public Set<String> getOpString() {
		return semantcis;
	}

	public void setOpString(String opString) {
		if (!this.semantcis.contains(opString)) {
			this.semantcis.add(opString);
		}
	}

	public Set<String> getDateList() {
		return dates;
	}

	public void setDateList(String date) {
		if (!this.dates.contains(date)) {
			this.dates.add(date);
		}
	}

	public Set<String> getIndexList() {
		return indexs;
	}

	public void setIndexList(String index) {
		if (!this.indexs.contains(index)) {
			this.indexs.add(index);
		}
	}

	public Set<String> getStkCodeList() {
		return stkCodes;
	}

	public void setStkCodeList(String stkCode) {
		if (!this.stkCodes.contains(stkCode)) {
			this.stkCodes.add(stkCode);
		}
	}

	public boolean isEmpty() {
		return !(stkCodes.size() > 0 || indexs.size() > 0 || indexs.size() > 0 || semantcis.size() > 0);
	}
}
