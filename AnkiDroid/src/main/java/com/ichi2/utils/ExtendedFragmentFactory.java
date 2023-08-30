/*
 Copyright (c) 2021 Tarek Mohamed Abdalla <tarekkma@gmail.com>

 This program is free software; you can redistribute it and/or modify it under
 the terms of the GNU General Public License as published by the Free Software
 Foundation; either version 3 of the License, or (at your option) any later
 version.

 This program is distributed in the hope that it will be useful, but WITHOUT ANY
 WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 PARTICULAR PURPOSE. See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along with
 this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.ichi2.utils;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;

/**
 * A factory that enable extending another {@link FragmentFactory}.
 *
 * This should be useful if you want to add extra instantiations without overriding the instantiations in an old factory
 */
public abstract class ExtendedFragmentFactory extends FragmentFactory {

    @NonNull
    final protected FragmentFactory mBaseFactory;


    /**
     * Create an extended factory from a base factory
     */
    public ExtendedFragmentFactory(@NonNull FragmentFactory baseFactory) {
        mBaseFactory = baseFactory;
    }


    /**
     * Create an extended factory from the factory on an activity, and update the activity fragment factory
     */
    public ExtendedFragmentFactory(@NonNull AppCompatActivity activity) {
        this(activity.getSupportFragmentManager().getFragmentFactory());
        activity.getSupportFragmentManager().setFragmentFactory(this);
    }

    /**
     * Typically you want to return the result of a super call as the last result, so if the passed class couldn't be
     * instantiated by the extending factory, the base factory should instantiate it.
     */
    @NonNull
    @CallSuper
    @Override
    public Fragment instantiate(@NonNull ClassLoader classLoader, @NonNull String className) {
        return mBaseFactory.instantiate(classLoader, className);
    }
}
