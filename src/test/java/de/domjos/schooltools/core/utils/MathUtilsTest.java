/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.core.utils;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author Dominic Joas
 */

public class MathUtilsTest {

    @Test
    public void roundTest() {
        assertEquals(MathUtils.round(2.25, 0.5), 2.5, 0.0);
        assertEquals(MathUtils.round(2.225, 0.05), 2.25, 0.0);
    }

    @Test
    public void extendedRoundTest() {
        final double VAL = 4.334615384615384;
        assertEquals(MathUtils.round(VAL, 0.1), 4.3, 0.0);
    }
}
