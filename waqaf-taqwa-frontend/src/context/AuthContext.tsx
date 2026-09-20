import React, { createContext, useContext, useState } from 'react';

export type UserRole = 'personal' | 'merchant' | 'admin';

export interface UserSession {
  id: string;
  name: string;
  phoneNumber: string;
  role: UserRole;
}

interface AuthContextType {
  user: UserSession | null;
  role: UserRole;
  isAuthenticated: boolean;
  setRole: (role: UserRole) => void;
  login: (role: UserRole) => void;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<UserSession | null>({
    id: 'usr_01',
    name: 'Pewakaf Taqwa',
    phoneNumber: '+60123456789',
    role: 'personal',
  });

  const setRole = (role: UserRole) => {
    if (user) {
      setUser({ ...user, role });
    }
  };

  const login = (role: UserRole) => {
    setUser({
      id: 'usr_01',
      name: 'Pewakaf Taqwa',
      phoneNumber: '+60123456789',
      role,
    });
  };

  const logout = () => setUser(null);

  return (
    <AuthContext.Provider
      value={{
        user,
        role: user?.role || 'personal',
        isAuthenticated: !!user,
        setRole,
        login,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used within an AuthProvider');
  return context;
};