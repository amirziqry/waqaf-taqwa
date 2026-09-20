import api from '../client';


export const me = async () => {
    const response = await api.get(
        '/account/auth/me'
    );

    return {
        username: response.data.username,
        role: response.data.role, // Only for admin, else null (ADMIN/EDITOR/USER)
    }
};
